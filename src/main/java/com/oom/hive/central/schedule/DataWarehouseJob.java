package com.oom.hive.central.schedule;

import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.oom.hive.central.repository.HiveBotEventsRepository;
import com.oom.hive.central.repository.HiveBotRepository;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.repository.model.HiveBotEvent;
import org.bson.Document;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.diagnostics.Loggers;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.bson.codecs.configuration.CodecRegistry;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class DataWarehouseJob {
    @Value("${app.warehouse.mongodb.hostname}")
    private String dbHostname;

    @Value("${app.warehouse.mongodb.port}")
    private int dbPort;

    @Value("${app.warehouse.mongodb.database}")
    private String database;

    @Value("${app.warehouse.mongodb.username}")
    private String username;

    @Value("${app.warehouse.mongodb.password}")
    private String password;

    @Autowired
    HiveBotRepository hiveBotRepository;

    @Autowired
    HiveBotEventsRepository hiveBotEventsRepository;


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DataWarehouseJob.class);

    //every 15 mins , Start after 1 min.
    //@Scheduled(fixedDelay = 1000 * 60 * 15, initialDelay = 1000 * 60 * 1)
    @Scheduled(fixedDelay = 1000 * 60 * 1) ///initialDelay = 1000 * 60 * 1
    public void runDataMove() {

        MongoClient mongo = connectWarehouse();

        if(mongo!=null){
            try {
                /*CodecRegistry pojoCodecRegistry =
                        fromRegistries(MongoClient.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));

                Loggers.getLogger("");
                */

                CodecRegistry testCodecReg =
                        fromRegistries(MongoClient.getDefaultCodecRegistry(),
                                fromProviders(new DocumentCodecProvider()));

                MongoDatabase mdW = mongo.getDatabase(this.database);
                mdW = mdW.withCodecRegistry(testCodecReg);

                MongoCollection<HiveBot> colBots = mdW.getCollection("hiveBot", HiveBot.class);
                hiveBotRepository.findAll()
                        .stream().forEach(bot -> {
                    colBots.replaceOne(eq("botId", bot.getBotId()), bot);
                });

                //copyBotsToCollection(hiveBotRepository.findAll(), colBots);

           /* colBots = mdW.getCollection("hiveBot");
            hiveBotRepository.findAll()
                    .stream()
                    .forEach(System.out::println);*/

            /*copyBotEventsToCollection(
                    hiveBotEventsRepository.findAll(),
                    mdW.getCollection("hiveBotEvent")
            );*/

                mongo.close();
                logger.info("All data has been Warehoused. Haven't cleaned up though.");
            }catch (Exception a){
                a.printStackTrace();
            }
        }


    }


    /*private void copyToCollection(List<HiveBotEvent> pojoObjects, MongoCollection dbCollection){
        Gson gson = new Gson();

        pojoObjects.stream()
                //.map( obj ->new BasicDBObject(obj.getBotId(), gson.toJson(obj) ))
                .map( obj ->{
                    Document d = Document.parse(gson.toJson(obj) );
                    d.put("_id",obj.getBotId());
                    return d;
                })
                .forEach(dbCollection::insertOne);
    }*/

    private void copyBotsToCollection(List<HiveBot> pojoObjects, MongoCollection dbCollection){
        /*
         * Wah lah! . the crap i need to to take care moving from JPA to Custom Implementation.
         * Mongo Future ready, Psst!.
         */
        Gson gson = new Gson();
        pojoObjects.stream()
                //.map( obj ->new BasicDBObject(obj.getBotId(), gson.toJson(obj) ))
                /*.map( obj ->{
                    Document d = Document.parse(gson.toJson(obj) );
                    d.put("_id",obj.getBotId());
                    return d;
                })*/
                //.forEach(dbCollection::updateOne)
                .forEach( d ->{
                    //gson.toJson(d);
                    //Bson filter = Filters.eq("_id", d.get("botId"));
                    BasicDBObject query = new BasicDBObject();
                    query.append("_id", d.getBotId());
                    //System.err.println(d.get("botId"));
                    UpdateOptions options = new UpdateOptions().upsert(true);

                    Document docObject = Document.parse(gson.toJson(d));
                    docObject.put("_id",d.getBotId());


                    dbCollection.replaceOne(query,docObject,options);
                });
        //
        // ;
    }


    private void copyBotEventsToCollection(List<HiveBotEvent> pojoObjects, MongoCollection dbCollection){
        /*
         * Wah lah! . the crap i need to to take care moving from JPA to Custom Implementation.
         * Mongo Future ready, Psst!.
         */
        Gson gson = new Gson();
        pojoObjects.stream()
                //.map( obj ->new BasicDBObject(obj.getBotId(), gson.toJson(obj) ))
                /*.map( obj ->{
                    Document d = Document.parse(gson.toJson(obj) );
                    d.put("_id",obj.getBotId());
                    return d;
                })*/
                //.forEach(dbCollection::updateOne)
                .forEach( d ->{
                    //gson.toJson(d);
                    //Bson filter = Filters.eq("_id", d.get("botId"));
                    BasicDBObject query = new BasicDBObject();
                    query.append("_id", d.getEventId());
                    //System.err.println(d.get("botId"));
                    UpdateOptions options = new UpdateOptions().upsert(true);

                    Document docObject = Document.parse(gson.toJson(d));
                    docObject.put("_id",d.getEventId());


                    dbCollection.replaceOne(query,docObject,options);
                });
        //
        // ;
    }

    private MongoClient connectWarehouse() {
        MongoClient mongo = null;



        if (!StringUtils.isEmpty(username)){
            MongoCredential credential;
            List<MongoCredential> credentials = new ArrayList<>();
            credential = MongoCredential.createCredential(this.username,
                    this.database,
                    this.password.toCharArray());
            credentials.add(credential);
            ServerAddress address = new ServerAddress(dbHostname, dbPort);
            mongo = new MongoClient(address,credentials);
        }else{
            mongo = new MongoClient(dbHostname, dbPort);
        }
        try {
            mongo.getAddress();
        } catch (Exception e) {
            logger.warn("Warehouse Database down : err: " + e.getLocalizedMessage());
            mongo.close();
            return null;
        }
        return mongo;
    }
}
