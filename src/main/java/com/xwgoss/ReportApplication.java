package com.xwgoss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.xwgoss.service.CoreService;

@SpringBootApplication
public class ReportApplication {

	public static void main(String[] args) {
		SpringApplication springApplication=new SpringApplication(ReportApplication.class);
		springApplication.addListeners(new CoreService());
		springApplication.run(args);
	}
}
