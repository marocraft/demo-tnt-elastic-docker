package com.github.marocraft.demo.demotnt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.marocraft.trackntrace.annotation.EnableTracknTrace;

@EnableTracknTrace
@SpringBootApplication
public class DemoTntApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoTntApplication.class, args);
	}

}
