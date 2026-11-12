package com.edwin.citas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.edwin.citas","com.edwin.commons"})
public class CitasApplication {
	public static void main(String[] args) {
		SpringApplication.run(CitasApplication.class, args);
	}
}
