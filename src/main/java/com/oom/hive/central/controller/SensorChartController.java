package com.oom.hive.central.controller;

import com.oom.hive.central.model.ChartJSData;
import com.oom.hive.central.model.HiveCentralResponse;
import com.oom.hive.central.model.charting.sensordata.TemperatureHumidity;
import com.oom.hive.central.service.ChartingService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@CrossOrigin(
        maxAge = 3600 ,
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST}
)
@RestController
@RequestMapping("/sensorchart")
@Api(value="sensorchart", description="Charts from SensorData")
public class SensorChartController {

    @Autowired
    ChartingService chartingService;

    @RequestMapping(value= "/{hiveBotId}/temp_humidity", method = RequestMethod.GET )
    public ResponseEntity<ChartJSData> getTemperatureAndHumidity(
            @PathVariable(value = "hiveBotId") String hiveBotId,
            @RequestParam(value="flashbackMinutes" , defaultValue = "360" /*60*6 , 6 Hours */) int flashbackMinutes,
            @RequestParam(value="intervalMinutes" , defaultValue = "15" /*Every 15 Minutes */) int intervalMinutes
    ){

        ChartJSData chartData = chartingService.getEventsForTimeSeries(
                hiveBotId,
                new String[]{"Temperature","HumidityPercent"},
                flashbackMinutes,
                intervalMinutes);

        return new ResponseEntity(chartData,HttpStatus.ACCEPTED);

    }

}
