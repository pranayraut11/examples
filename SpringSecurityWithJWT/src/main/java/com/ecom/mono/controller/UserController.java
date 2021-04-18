package com.ecom.mono.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ecom.mono.dto.UserDTO;
import com.ecom.mono.dto.UserLoginRequest;
import com.ecom.mono.entity.User;
import com.ecom.mono.repository.UserRepository;
import com.ecom.mono.utility.JwtUtility;

@RestController
@RequestMapping("user")
public class UserController {
	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtility jwtUtility;

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
		return userRepo.findById(userId).orElseThrow(() -> new RuntimeException("Username not found"));
	}

	@PostMapping("/authenticate")
	public String authenticateUser(@RequestBody UserLoginRequest userDTO) {

		authenticate(userDTO.getUsername(), userDTO.getPassword());

		return jwtUtility.generateToken(userDTO.getUsername());
	}

	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

}
