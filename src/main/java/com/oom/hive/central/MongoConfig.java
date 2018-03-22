package com.oom.hive.central;


import com.mongodb.MongoClient;
//import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.util.Arrays;

@Configuration
public class MongoConfig {

    private static final org.slf4j.Logger logger =
            LoggerFactory.getLogger(MongoConfig.class);


    @Value( "${app.mongodb.hostname}" )
    String hostname;

    @Value( "${app.mongodb.port}" )
    int port;

    @Value( "${app.mongodb.database}" )
    String database;

    @Value( "${app.mongodb.username}" )
    String username;

    @Value( "${app.mongodb.password}" )
    String password;

    @Bean
    public MongoTemplate mongoTemplate() {

        MongoClient mongoClient;

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            logger.warn("No UserName or Password set. Will attempt connecting directly");
            mongoClient = new MongoClient(hostname,port);
        }else{
            logger.warn("Connecting with username: " + username + " password:XXXXXX");
            MongoCredential mongodbConnectCreds =
                    MongoCredential.createCredential(
                            username,
                            database,
                            password.toCharArray()
                    );
            ServerAddress mongoServerAddress =
                    new ServerAddress(hostname, port);
            mongoClient = new MongoClient(
                    mongoServerAddress,
                    Arrays.asList(mongodbConnectCreds)
            );
        }


        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, database);
        return mongoTemplate;
    }

}
