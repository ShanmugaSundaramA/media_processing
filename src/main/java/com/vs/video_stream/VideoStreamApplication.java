package com.vs.video_stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableMongoAuditing
@EnableWebSecurity
public class VideoStreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoStreamApplication.class, args);
	}

}
