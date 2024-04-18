package com.security.oauth.authserver.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.Date;
import java.util.Set;

@Document
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String clientId;
    private String clientSecret;
    private Set<ClientAuthenticationMethod> authenticationMethods;
    private Set<AuthorizationGrantType> authorizationGrantTypes;
    private Set<String> redirectUris;
    private Set<String> scopes;


    public static RegisteredClient toRegisteredClient(Client client){
        return RegisteredClient.withId(client.getClientId())
                .clientId(client.getClientId())
                .clientSecret(client.getClientSecret())
                .clientAuthenticationMethods(clientAuthenticationMethods -> clientAuthenticationMethods.addAll(client.getAuthenticationMethods()))
                .authorizationGrantTypes(authorizationGrantTypes -> authorizationGrantTypes.addAll(client.getAuthorizationGrantTypes()))
                .redirectUris(uris-> uris.addAll(client.getRedirectUris()))
                .scopes(scopes-> scopes.addAll(client.getScopes()))
                .clientIdIssuedAt( new Date().toInstant()).tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofMinutes(60L)).build())
                .build();
    }




}
