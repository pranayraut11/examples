package com.security.jwtexample.service;

import com.security.jwtexample.dto.LoginResponse;
import com.security.jwtexample.dto.UserLogin;
import com.security.jwtexample.entity.UserInfo;
import com.security.jwtexample.repository.UserRepository;
import com.security.jwtexample.utility.JWTUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(String username, String password) throws AuthenticationException {
        var user = userRepository.findByUsername(username).orElseThrow();
        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);
        if(authenticationResponse.isAuthenticated()){
            return new LoginResponse(JWTUtility.generateToken(username));
        }
        throw new AuthenticationException("Invalid username/password");
    }

    @Override
    public void register(UserLogin userLogin) {
        userRepository.save(new UserInfo(userLogin.getUsername(), passwordEncoder.encode(userLogin.getPassword()), Collections.singleton("ROLE_USER")));
    }

    @Override
    public UserInfo getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

}
