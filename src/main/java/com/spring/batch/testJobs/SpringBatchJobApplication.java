package com.spring.batch.testJobs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.spring.batch.jobRestController.JobRestResource;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class SpringBatchJobApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchJobApplication.class, args);
	}

}
