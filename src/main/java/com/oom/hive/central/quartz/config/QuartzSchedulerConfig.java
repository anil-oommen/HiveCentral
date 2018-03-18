package com.oom.hive.central.quartz.config;

import org.springframework.context.annotation.Configuration;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class QuartzSchedulerConfig {
    public static final String QUARTZ_PROPERTIES_PATH = "/quartz.properties";

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) throws   IOException {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setAutoStartup(true);
        factory.setJobFactory(jobFactory);
        factory.setQuartzProperties(quartzProperties());
        return factory;
    }

    @Bean
    public Properties quartzProperties() throws IOException{
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_PROPERTIES_PATH));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}
