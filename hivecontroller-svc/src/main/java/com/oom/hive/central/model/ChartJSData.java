package com.oom.hive.central.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oom.hive.central.model.charting.sensordata.TemperatureHumidity;

import java.util.HashMap;
import java.util.Map;

public class ChartJSData {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String intervalFrequency;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    TemperatureHumidity data[] =  new TemperatureHumidity[0];

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntervalFrequency() {
        return intervalFrequency;
    }

    public void setIntervalFrequency(String intervalFrequency) {
        this.intervalFrequency = intervalFrequency;
    }

    public TemperatureHumidity[] getData() {
        return data;
    }

    public void setData(TemperatureHumidity[] data) {
        this.data = data;
    }
}
