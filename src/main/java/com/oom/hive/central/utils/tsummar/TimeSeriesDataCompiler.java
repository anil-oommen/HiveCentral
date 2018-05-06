package com.oom.hive.central.utils.tsummar;

import com.oom.hive.central.model.ChartJSData;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeSeriesDataCompiler {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TimeSeriesDataCompiler.class);


    public static void main(String a[]){
        TimeSeriesDataCompiler aaaa = new TimeSeriesDataCompiler();
        aaaa.prepareTimeSeriesData(
                60*6,5*3);
    }

    private ChartJSData prepareTimeSeriesData(
                                             int flashBackMinutes,
                                             int intervalMinutes){
        ChartJSData chartJsData = new ChartJSData();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime ldtEndWindow = LocalDateTime.now();
        LocalDateTime ldtStartWindow = LocalDateTime.now();
        ldtStartWindow = ldtStartWindow.minusMinutes((long)flashBackMinutes+intervalMinutes);
        ldtStartWindow = ldtStartWindow.minusMinutes((ldtStartWindow.getMinute()%intervalMinutes));
        ldtStartWindow = ldtStartWindow.minusSeconds(ldtStartWindow.getSecond());

        if(logger.isInfoEnabled()) {
            logger.info("../.../..../");
            logger.info("from {}", ldtStartWindow.format(formatter));
            logger.info("to {}", ldtEndWindow.format(formatter));
            while (ldtStartWindow.isBefore(ldtEndWindow)) {

                logger.info(ldtStartWindow.format(formatter));
                logger.info("   {}", (ldtStartWindow.getMinute() % intervalMinutes));
                ldtStartWindow = ldtStartWindow.plusMinutes(intervalMinutes);
            }
        }
        return chartJsData;
    }

}
