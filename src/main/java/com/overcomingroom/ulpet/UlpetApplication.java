package com.overcomingroom.ulpet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UlpetApplication {

	public static void main(String[] args) {
		SpringApplication.run(UlpetApplication.class, args);
	}

}
