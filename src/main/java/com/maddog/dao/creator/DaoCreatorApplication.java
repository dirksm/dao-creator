package com.maddog.dao.creator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class DaoCreatorApplication {
	
	public static void main(String[] args) {
		try {
			SpringApplication.run(DaoCreatorApplication.class, args);
		} catch (Exception e) {
			
		}
	}
}
