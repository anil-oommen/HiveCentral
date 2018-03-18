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
	@PropertySource("classpath:application.private.properties"),
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
			//testRepository();


		};
	}

	/*private void testRepository(){
		System.out.println(">Listing all Clients");
		for(BotClient bc :clientRepository.findAll()){
			System.out.println("\t>" + bc.toString());
		}

		BotClient testClient = new BotClient();
		testClient.setBotId("TEST.DISCARD");
		testClient.setStatus("UNKNOWN");
		testClient.setLastHearbeat(new java.util.Date());
		testClient.setBotVersion("1.0");
		testClient.getDataSet().add(new BotData("ONE","ONNUE"));
		testClient.getDataSet().add(new BotData("TWO","RUNDU"));
		//testClient.getDataMap().put("ONE",new BotData("ONNU","1"));
		//testClient.getDataMap().put("TWO",new BotData("RANDU","2"));
		System.out.println(">Saving Test Client");
		clientRepository.save(testClient);

		for(BotClient bc :clientRepository.findAll()){
			System.out.println("\t>" + bc.toString());
		}

		clientRepository.delete(testClient);
		for(BotClient bc :clientRepository.findAll()){
			System.out.println("\t>" + bc.toString());
		}
	}*/
}
