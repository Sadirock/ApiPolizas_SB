package com.segurosbolivar.polizasapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.segurosbolivar.polizasapp.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class PolizasappApplication {

	public static void main(String[] args) {
		SpringApplication.run(PolizasappApplication.class, args);
	}

}
