package com.badeling.msbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.badeling.msbot.domain.ReRead;

import java.util.TimeZone;

@SpringBootApplication
@EnableConfigurationProperties({ReRead.class})
public class MsbotApplication {
	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		SpringApplication.run(MsbotApplication.class, args);
	}

}
