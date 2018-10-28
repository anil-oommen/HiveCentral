package com.oom.hive.central;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.sun.javafx.binding.StringFormatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
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

        final MongoClientOptions options = MongoClientOptions.builder()
                //.connectTimeout(2)
                //.maxWaitTime(1000)
                //.socketTimeout(1000)
        .minHeartbeatFrequency(5000)
        .build();


        MongoClient mongoClient;

        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
            logger.info(String.format("Connecting anonymous , %s:%d",hostname, port));
            mongoClient = new MongoClient(new ServerAddress(hostname,port),options);
        }else{
            logger.info(String.format("Connecting as '%s' , %s:%d",username, hostname, port));
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
                    Arrays.asList(mongodbConnectCreds),options
            );
        }



        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, database);
        //mongoClient.

        

        return mongoTemplate;
    }

}
