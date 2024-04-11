package com.security.jwtexample.service;

import com.security.jwtexample.dto.LoginResponse;
import com.security.jwtexample.dto.UserLogin;
import com.security.jwtexample.entity.UserInfo;

import javax.naming.AuthenticationException;

public interface UserService {

    public LoginResponse login(String username, String password) throws AuthenticationException;
    public void register(UserLogin userLogin);
    public UserInfo getUser(String username);

}
