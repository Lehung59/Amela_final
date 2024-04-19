package com.example.demo;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
//	@Autowired
//	private UserRepo userRepo;
//
//	@Override
//	public void run(String... args) throws Exception{
//		User user1 = new User();
//		user1.setFirstName("Le");
//		user1.setLastName("Hung");
//		user1.setEmail("hung@gmail.com");
//		user1.setPassword("123456");
//		userRepo.save(user1);
	}





