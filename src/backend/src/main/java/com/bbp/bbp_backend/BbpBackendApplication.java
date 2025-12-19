package com.bbp.bbp_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BbpBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BbpBackendApplication.class, args);
	}

}
