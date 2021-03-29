package com.ecom.mono.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.mono.entity.User;
import com.ecom.mono.repository.UserRepository;

@RestController
@RequestMapping("user")
public class UserController {
	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserRepository userRepo;

	@GetMapping
	public List<User> getAllUser() {
		return userRepo.findAll();
	}

	@PostMapping
	public User createUser(@RequestBody User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		user.setRole("USER");
		return userRepo.save(user);
	}

	@GetMapping("/{userId}")
	public User getUser(@PathVariable String userId) {
		return userRepo.findById(userId).orElseThrow(()-> new RuntimeException("Username not found"));
	}

}
