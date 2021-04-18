package com.ecom.mono.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "User")
@Getter
@Setter
public class User {

	@Id
	private String id;
	
	private String username;
	
	private String password;
	
	private String role;
}
