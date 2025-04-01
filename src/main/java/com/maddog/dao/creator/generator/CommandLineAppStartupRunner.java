package com.maddog.dao.creator.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
 
	@Autowired
	private MainGenerator mainGenerator;
	

    @Override
    public void run(String...args) throws Exception {
		log.info("Generating from Main Generator....");
        mainGenerator.generate();
    }
}