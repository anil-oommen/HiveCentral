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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime ldtEndWindow = LocalDateTime.now();
        LocalDateTime ldtStartWindow = LocalDateTime.now();
        ldtStartWindow = ldtStartWindow.minusMinutes((long)flashBackMinutes+intervalMinutes);
        ldtStartWindow = ldtStartWindow.minusMinutes((ldtStartWindow.getMinute()%intervalMinutes));
        ldtStartWindow = ldtStartWindow.minusSeconds(ldtStartWindow.getSecond());

        System.out.println("../.../..../");
        System.out.println("from " + ldtStartWindow.format(formatter));
        System.out.println("to " + ldtEndWindow.format(formatter));
        while(ldtStartWindow.isBefore(ldtEndWindow)){

            System.out.print(ldtStartWindow.format(formatter));
            System.out.print("   " + (ldtStartWindow.getMinute()%intervalMinutes));



            System.out.println();
            ldtStartWindow = ldtStartWindow.plusMinutes(intervalMinutes);
        }
        return chartJsData;
    }

}
