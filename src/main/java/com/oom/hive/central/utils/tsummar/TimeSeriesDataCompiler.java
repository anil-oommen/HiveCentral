package com.oom.hive.central.utils.tsummar;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.oom.hive.central.model.ChartJSData;
import com.oom.hive.central.repository.HiveBotEventsRepository;
import com.oom.hive.central.repository.model.HiveBotEvent;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.lt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Consumer;

public class TimeSeriesDataCompiler {

    public TimeSeriesDataCompiler()
    {

    }

    public static void addEventData(HiveBotEvent hiveBoteEvent){

    }


    public static void main(String a[]){
        TimeSeriesDataCompiler aaaa = new TimeSeriesDataCompiler();
        aaaa.prepareTimeSeriesData("",new String[]{"",""},
                60*6,5*3);
    }

    public ChartJSData prepareTimeSeriesData(String botId,
                                             String[] seriesKeyName,
                                             int flashBackMinutes,
                                             int intervalMinutes){
        ChartJSData chartJsData = new ChartJSData();
        /*
        MongoClient mongoClient = new MongoClient("192.168.1.100", 27017);
        MongoDatabase mongoDb = mongoClient.getDatabase("hivecentral_db");
        MongoCollection dbCollHiveBotEvent = mongoDb.getCollection("hiveBotEvent");
        Bson query = and(
                eq("botId","OOMM.HIVE MICLIM.02"),
                in("key","Temperature","HumidityPercent")
        );
        FindIterable<HiveBotEvent> resultData = dbCollHiveBotEvent.find(query).;
        resultData.forEach((Consumer<HiveBotEvent>) event -> {
            System.out.println(event.botId);
        });

        HiveBotEventsRepository hr ;
        hr.find
        */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime ldtEndWindow = LocalDateTime.now();
        LocalDateTime ldtStartWindow = LocalDateTime.now();
        ldtStartWindow = ldtStartWindow.minusMinutes(flashBackMinutes+intervalMinutes);
        ldtStartWindow = ldtStartWindow.minusMinutes((ldtStartWindow.getMinute()%intervalMinutes));
        ldtStartWindow = ldtStartWindow.minusSeconds(ldtStartWindow.getSecond());

        System.out.println("../.../..../");
        System.out.println("from " + ldtStartWindow.format(formatter));
        System.out.println("to " + ldtEndWindow.format(formatter));
        while(ldtStartWindow.isBefore(ldtEndWindow)){

            System.out.print(ldtStartWindow.format(formatter));
            System.out.print("   " + (ldtStartWindow.getMinute()%intervalMinutes));


            //System.out.print("    " + ldtStartWindow.format(formatter));
            //System.out.print("   " + (ldtStartWindow.getMinute()%intervalMinutes));

            System.out.println();
            ldtStartWindow = ldtStartWindow.plusMinutes(intervalMinutes);
        }
        return chartJsData;
    }

}
