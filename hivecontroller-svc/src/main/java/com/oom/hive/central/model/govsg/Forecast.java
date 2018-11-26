package com.oom.hive.central.model.govsg;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "area",
        "forecast"
})
public class Forecast {
    @JsonProperty("area")
    private String area;
    @JsonProperty("forecast")
    private String forecast;
    @JsonProperty("area")
    public String getArea() {
        return area;
    }
    @JsonProperty("area")
    public void setArea(String area) {
        this.area = area;
    }
    @JsonProperty("forecast")
    public String getForecast() {
        return forecast;
    }
    @JsonProperty("forecast")
    public void setForecast(String forecast) {
        this.forecast = forecast;
    }
}