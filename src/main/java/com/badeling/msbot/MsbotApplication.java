package com.badeling.msbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.badeling.msbot.domain.ReRead;


@SpringBootApplication
@EnableConfigurationProperties({ReRead.class})
public class MsbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsbotApplication.class, args);
	}

}
