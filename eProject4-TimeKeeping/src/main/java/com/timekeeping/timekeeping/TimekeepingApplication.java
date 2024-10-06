package com.timekeeping.timekeeping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TimekeepingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimekeepingApplication.class, args);
	}

}
