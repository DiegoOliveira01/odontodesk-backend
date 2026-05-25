package com.diego.odontodesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OdontodeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(OdontodeskApplication.class, args);
	}

}
