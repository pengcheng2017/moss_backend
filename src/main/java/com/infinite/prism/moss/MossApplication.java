package com.infinite.prism.moss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.infinite.prism.moss.mapper")
@SpringBootApplication
public class MossApplication {

	public static void main(String[] args) {
		SpringApplication.run(MossApplication.class, args);
	}

}
