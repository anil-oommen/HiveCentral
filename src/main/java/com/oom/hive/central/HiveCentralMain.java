package com.oom.hive.central;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.util.Arrays;

@SpringBootApplication
@EntityScan()
@PropertySources({
	@PropertySource("classpath:application.properties"),
	@PropertySource(value = "classpath:application.private.properties",ignoreResourceNotFound = true),
	@PropertySource("classpath:build.properties")
})
public class HiveCentralMain {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HiveCentralMain.class);


	@Value("${app.build.version}")
	private String app_build_version;

	@Value("${app.build.timestamp}")
	private String app_build_timestamp;

	public static void main(String[] args) {

		SpringApplication.run(HiveCentralMain.class, args);
	}


	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			logger.info("Application Build: " + app_build_version +
					" " + app_build_timestamp
			);
			//Inspect Bean Def
			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				//System.out.println(beanName);
			}
		};
	}

}
