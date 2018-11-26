package com.oom.hive.central.model.govsg;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "start",
        "end"
})
public class ValidPeriod {
    @JsonProperty("start")
    private String start;
    @JsonProperty("end")
    private String end;
    @JsonProperty("start")
    public String getStart() {
        return start;
    }
    @JsonProperty("start")
    public void setStart(String start) {
        this.start = start;
    }
    @JsonProperty("end")
    public String getEnd() {
        return end;
    }
    @JsonProperty("end")
    public void setEnd(String end) {
        this.end = end;
    }
}
