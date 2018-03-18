package com.oom.hive.central.service.impl;

import com.oom.hive.central.controller.BotController;
import com.oom.hive.central.model.ChartJSData;
import com.oom.hive.central.model.charting.sensordata.TemperatureHumidity;
import com.oom.hive.central.repository.HiveBotEventsRepository;
import com.oom.hive.central.repository.model.HiveBotEvent;
import com.oom.hive.central.service.ChartingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ChartingSerivceImpl implements ChartingService{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ChartingSerivceImpl.class);

    private static SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z");

    @Autowired
    HiveBotEventsRepository eventsRepository ;
    public ChartJSData getEventsForTimeSeries(
            String hiveBotId,
            String[] eventKeys,
            int flashBackMinutes, int intervalMinutes){

        ChartJSData chartJsData = new ChartJSData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        TemperatureHumidityTSeries thTSeries = new TemperatureHumidityTSeries(flashBackMinutes,  intervalMinutes);
        logger.info( "CHART Querying for Events on:" + hiveBotId +
                        "\n\t from:" + thTSeries.ldtStartWindowFrom.format(formatter) +
                        "\n\t to:" + thTSeries.ldtEndWindow.format(formatter) +
                        "\n\t FlashBack(Minutes) :" + flashBackMinutes +
                        "\n\t Interval:(Minutes)" + intervalMinutes
        );
        try (Stream<HiveBotEvent> eventStream = eventsRepository
                .findInTimeSeries(hiveBotId,
                        eventKeys,
                        java.sql.Timestamp.valueOf( thTSeries.ldtStartWindowFrom ),
                        new Sort(Sort.Direction.ASC, "time"))
        ) {
            eventStream.forEach(
                    event ->{
                        thTSeries.fitEventToTimeSeries(event);
                    }
            );
            eventStream.close();
        }


        //remove all 0 values .
        thTSeries.dataListTempHumidity.removeIf(thData-> thData.getTemperature()==0 );
        chartJsData.setData(
                thTSeries.dataListTempHumidity.toArray(
                    new TemperatureHumidity[thTSeries.dataListTempHumidity.size()]
                )
        );


        return chartJsData;
    }



    public class TemperatureHumidityTSeries{
        public LocalDateTime ldtEndWindow = null;
        public LocalDateTime ldtStartWindowFrom = null;
        public List<TemperatureHumidity> dataListTempHumidity = new ArrayList<TemperatureHumidity>();
        Iterator<TemperatureHumidity> itrTempHumdTS=  null;
        TemperatureHumidity currentTempHumdTS =  null;
        public TemperatureHumidityTSeries(int flashBackMinutes, int intervalMinutes){
            ldtEndWindow = LocalDateTime.now();
            ldtStartWindowFrom = _computeStartWindow(flashBackMinutes,intervalMinutes);

            LocalDateTime ldtPreComputeStartWindow = _computeStartWindow(flashBackMinutes,intervalMinutes);

            while(ldtPreComputeStartWindow.isBefore(ldtEndWindow)){
               dataListTempHumidity.add(new TemperatureHumidity(0,0,
                        java.sql.Timestamp.valueOf( ldtPreComputeStartWindow )
                ));
                ldtPreComputeStartWindow = ldtPreComputeStartWindow.plusMinutes(intervalMinutes);
            }

            itrTempHumdTS=  dataListTempHumidity.iterator();
            _moveNextTSeries(); //The first One.
        }

        private LocalDateTime _computeStartWindow(int flashBackMinutes, int intervalMinutes){
            LocalDateTime startDateTime = LocalDateTime.now();
            {
                //Adjust Window to closest rounded value.
                startDateTime = startDateTime.minusMinutes(flashBackMinutes + intervalMinutes);
                startDateTime = startDateTime.minusMinutes((startDateTime.getMinute() % intervalMinutes));
                startDateTime = startDateTime.minusSeconds(startDateTime.getSecond());
            }
            return startDateTime;
        }


        private boolean _moveNextTSeries(){
            if(itrTempHumdTS.hasNext()){
                TemperatureHumidity _oldCurrentTempHumdTS = currentTempHumdTS;
                currentTempHumdTS =  itrTempHumdTS.next();
                if(_oldCurrentTempHumdTS!=null && currentTempHumdTS!=null){
                    currentTempHumdTS.setTemperature(_oldCurrentTempHumdTS.getTemperature());
                    currentTempHumdTS.setHumidity(_oldCurrentTempHumdTS.getHumidity());
                }

                logger.debug("CHART TInterval:" +
                        ((_oldCurrentTempHumdTS!=null)?sdf.format(_oldCurrentTempHumdTS.getSeriesDate()):"") + "  --->--- " +
                        ((currentTempHumdTS!=null)?sdf.format(currentTempHumdTS.getSeriesDate()):"") + " .next()" );
                return true;
            }else{
                TemperatureHumidity _oldCurrentTempHumdTS = currentTempHumdTS;
                currentTempHumdTS=null;
                logger.debug("CHART TInterval:" +
                        ((_oldCurrentTempHumdTS!=null)?sdf.format(_oldCurrentTempHumdTS.getSeriesDate()):"") + "  --->--- " +
                         " FINISHED " );

                return false;
            }
        }

        void fitEventToTimeSeries(HiveBotEvent hvBotEvent){

            if(currentTempHumdTS!=null && hvBotEvent.time.after(currentTempHumdTS.getSeriesDate())){
                //Time to move to next TimeSeries
                _moveNextTSeries();
            }

            String actionToTake = "";

            if(currentTempHumdTS==null){
                actionToTake = " IGNORE. Not Fitted";
            }else{
                if("Temperature".equals(hvBotEvent.getKey())){
                    currentTempHumdTS.setTemperature(Float.parseFloat(hvBotEvent.getValue()));
                }else if("HumidityPercent".equals(hvBotEvent.getKey())) {
                    currentTempHumdTS.setHumidity(Float.parseFloat(hvBotEvent.getValue()));
                }else{
                    actionToTake = " IGNORE. EventKey not expected. Possibly NoSQL Filter is incorrect";
                }
            }
            logger.debug("CHART EventData:" + sdf.format(hvBotEvent.getTime())
                    + " " + hvBotEvent.getKey() + ":"
                    + hvBotEvent.getValue() + "  " +
                    actionToTake
            );

        }

    }
}
