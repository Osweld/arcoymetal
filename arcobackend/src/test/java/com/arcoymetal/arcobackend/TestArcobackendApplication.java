package com.arcoymetal.arcobackend;

import org.springframework.boot.SpringApplication;

public class TestArcobackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(ArcobackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
