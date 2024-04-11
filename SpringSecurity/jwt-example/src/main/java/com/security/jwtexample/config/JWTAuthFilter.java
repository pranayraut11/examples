package com.security.jwtexample.config;

import com.security.jwtexample.service.UserDetailsServiceImpl;
import com.security.jwtexample.utility.JWTUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@Slf4j
public class JWTAuthFilter extends OncePerRequestFilter {


    @Autowired
    private UserDetailsServiceImpl userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String username = null;
        String jwtToken = null;
        String token = request.getHeader("Authorization");
        log.info("Token  {} ", token);
        if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
            jwtToken = token.substring(7);
            log.info("Token  {} ", jwtToken);
            username = JWTUtility.getUsername(jwtToken);
            log.info("Username   {} ", username);

        }

        if (!StringUtils.hasText(jwtToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userInfo = userService.loadUserByUsername(username);
            log.info("User Info {} ", userInfo);
            if (Objects.nonNull(userInfo) && JWTUtility.isValidToken(jwtToken, userInfo.getUsername())) {
                log.info("Token is valid");
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userInfo, null, userInfo.getAuthorities());
                authenticationToken.setDetails(userInfo);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }else {
                log.info("Token is not valid");
            }
            log.info("Token is not valid");
        }
        filterChain.doFilter(request, response);
    }
}
