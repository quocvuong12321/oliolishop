package com.oliolishop.oliolishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
public class OliolishopApplication {

	public static void main(String[] args) {
		SpringApplication.run(OliolishopApplication.class, args);
	}

}
