package com.oom.hive.central.model.charting.sensordata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TemperatureHumidity {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    float temperature;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    float humidity;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    long dt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnore()
    Date seriesDate;


    public TemperatureHumidity(float degCls, float humPerct , Date timestamp){
        this.temperature = degCls;
        this.humidity = humPerct;
        this.dt = timestamp.getTime()/1000;
        this.seriesDate = timestamp;
    }


    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public Date getSeriesDate() {
        return seriesDate;
    }

    public void setSeriesDate(Date seriesDate) {
        this.seriesDate = seriesDate;
    }
}
