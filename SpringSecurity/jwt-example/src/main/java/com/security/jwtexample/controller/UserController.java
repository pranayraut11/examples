package com.security.jwtexample.controller;

import com.security.jwtexample.dto.LoginResponse;
import com.security.jwtexample.dto.UserLogin;
import com.security.jwtexample.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity register(@RequestBody UserLogin loginRequest) {
        userService.register(loginRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody UserLogin loginRequest) throws AuthenticationException {
        return ResponseEntity.ok(userService.login(loginRequest.getUsername(), loginRequest.getPassword()));
    }

}
