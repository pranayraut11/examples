package com.ecom.mono.utility;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

	@Bean
	public PasswordEncoder encode() {
		return new BCryptPasswordEncoder();
	}
}
