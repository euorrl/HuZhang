package com.bbp.bbp_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
@SpringBootApplication
@MapperScan("com.bbp.bbp_backend.mapper")

public class BbpBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BbpBackendApplication.class, args);
	}

}
