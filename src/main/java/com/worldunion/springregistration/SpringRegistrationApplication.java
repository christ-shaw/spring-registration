package com.worldunion.springregistration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity
public class SpringRegistrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRegistrationApplication.class, args);
	}
}
