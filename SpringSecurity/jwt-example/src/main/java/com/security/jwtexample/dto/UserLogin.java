package com.security.jwtexample.dto;

import lombok.Data;

@Data
public class UserLogin {

    private String username;
    private String password;
}
