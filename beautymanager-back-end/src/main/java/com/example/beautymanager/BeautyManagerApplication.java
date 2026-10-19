package com.example.beautymanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BeautyManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeautyManagerApplication.class, args);
	}

}
