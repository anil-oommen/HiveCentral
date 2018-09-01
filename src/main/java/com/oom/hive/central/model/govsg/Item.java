
package com.oom.hive.central.model.govsg;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "update_timestamp",
    "timestamp",
    "valid_period",
    "forecasts"
})
public class Item {

    @JsonProperty("update_timestamp")
    private String updateTimestamp;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("valid_period")
    private ValidPeriod validPeriod;
    @JsonProperty("forecasts")
    private List<Forecast> forecasts = null;

    @JsonProperty("update_timestamp")
    public String getUpdateTimestamp() {
        return updateTimestamp;
    }

    @JsonProperty("update_timestamp")
    public void setUpdateTimestamp(String updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("valid_period")
    public ValidPeriod getValidPeriod() {
        return validPeriod;
    }

    @JsonProperty("valid_period")
    public void setValidPeriod(ValidPeriod validPeriod) {
        this.validPeriod = validPeriod;
    }

    @JsonProperty("forecasts")
    public List<Forecast> getForecasts() {
        return forecasts;
    }

    @JsonProperty("forecasts")
    public void setForecasts(List<Forecast> forecasts) {
        this.forecasts = forecasts;
    }

}
