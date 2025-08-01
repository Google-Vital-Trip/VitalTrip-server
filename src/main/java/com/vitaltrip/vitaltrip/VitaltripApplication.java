package com.vitaltrip.vitaltrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VitaltripApplication {

	public static void main(String[] args) {
		SpringApplication.run(VitaltripApplication.class, args);
	}

}
