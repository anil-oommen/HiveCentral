package com.oom.hive.central;


import com.mongodb.MongoClient;
//import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;

@Configuration
public class MongoConfig {

    @Value( "${mongodb.hostname}" )
    String hostname;

    @Value( "${mongodb.port}" )
    int port;

    @Value( "${mongodb.database}" )
    String database;

    @Value( "${mongodb.username}" )
    String username;

    @Value( "${mongodb.password}" )
    String password;



    /*@Bean
    @Profile("StandaloneTesting")
    public MongoTemplate mongoTemplateEmbeddedNonPersistant() throws IOException {
        EmbeddedMongoFactoryBean mongo = new EmbeddedMongoFactoryBean();
        mongo.setBindIp(MONGO_DB_URL);
        MongoClient mongoClient = mongo.getObject();
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, MONGO_DB_NAME);
        return mongoTemplate;
    }*/

    @Bean
    public MongoTemplate mongoTemplate() {
        MongoClient mongoClient = new MongoClient(hostname,port);
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, database);
        return mongoTemplate;
    }

}
