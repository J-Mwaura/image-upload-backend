package com.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ImageUploadAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageUploadAppApplication.class, args);
	}

	@Autowired
	private Environment environment;

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {

			@Override
			public void addCorsMappings(CorsRegistry registry) {

				String[] allowedOrigins = environment.getProperty("cors.allowed.origin").split(",");

				registry.addMapping("/api/file/saveFile").allowedOrigins(allowedOrigins).allowedMethods("POST", "OPTIONS")
						.allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin");

				registry.addMapping("/api/file/getFiles").allowedOrigins(allowedOrigins).allowedMethods("GET", "OPTIONS")
						.allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin");

				registry.addMapping("/api/file/{id}")
						.allowedOrigins(allowedOrigins).allowedMethods("GET", "OPTIONS")
						.allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin");

				registry.addMapping("/api/file/{id}")
						.allowedOrigins(allowedOrigins).allowedMethods("PUT", "OPTIONS")
						.allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin");

				registry.addMapping("/api/file/{id}")
						.allowedOrigins(allowedOrigins).allowedMethods("DELETE", "OPTIONS")
						.allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept", "Origin");

			}
		};
	}

}
