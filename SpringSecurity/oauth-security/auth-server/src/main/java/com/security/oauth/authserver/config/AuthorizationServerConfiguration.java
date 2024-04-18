package com.security.oauth.authserver.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.security.oauth.authserver.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class AuthorizationServerConfiguration {
    public static final String ROLES = "roles";
    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${key.file}")
    private String fileName;
    @Value("${key.alias}")
    private String alias;
    @Value("${key.storepass}")
    private String password;

    @Value("${auth.server.url}")
    private String authServerUrl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientService clientService;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());
        return http.userDetailsService(userDetailsService).formLogin(Customizer.withDefaults()).build();
    }


    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource){
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    /**
     * Load the JWKSet from the keystore.
     * Use JWKSource to select the JWKSet
     */
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        JWKSet jwkSet =   jwkSet();
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    /**
     * Load the JWKSet from the keystore.
     * The keystore is loaded from the classpath, The keystore is a PKCS12 keystore,
     * The keystore is loaded using the password
     */
    private JWKSet jwkSet() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try(InputStream fis = this.getClass().getClassLoader().getResourceAsStream(fileName);) {
            keyStore.load(fis, password.toCharArray());
            return JWKSet.load(keyStore, name -> password.toCharArray());
        }
    }

    /**
     * Configure issuer url
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().issuer(authServerUrl).build();
    }

    /**
     * Register client with client details
     */
//    @Bean
//    public RegisteredClientRepository registeredClientRepository() {
//        RegisteredClient registeredClient = RegisteredClient.withId("couponservice").clientId("couponclient")
//                .clientSecret(passwordEncoder.encode("couponsecret")).clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
//                .redirectUri("http://localhost:8080/debug").scope(OidcScopes.OPENID).tokenSettings(tokenSettings()).build();
//        return new InMemoryRegisteredClientRepository(registeredClient);
//    }

    /**
     * Sets token expiration time
     */
//    @Bean
//    public TokenSettings tokenSettings(){
//        return TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(30L)).build();
//    }

    /**
     * Add roles to the token
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(){
        return context -> {
            if(context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)){
             Set<String> roles =  context.getPrincipal().getAuthorities().stream().
                     map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
             context.getClaims().claim(ROLES, roles);
            }
        };
    }
}
