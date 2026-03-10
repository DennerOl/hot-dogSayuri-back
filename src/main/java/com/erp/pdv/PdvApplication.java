package com.erp.pdv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PdvApplication implements CommandLineRunner {

	@Autowired
	// private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(PdvApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Example usage of PasswordEncoder
		// System.out.println(new BCryptPasswordEncoder().encode("123456"));

	}
}