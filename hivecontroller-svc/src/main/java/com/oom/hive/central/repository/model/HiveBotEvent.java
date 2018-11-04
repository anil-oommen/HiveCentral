package com.oom.hive.central.repository.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oom.hive.central.AppSettings;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HiveBotEvent {

    private String botId;
    @JsonFormat(pattern= AppSettings.TSTAMP_FORMAT)
    private Date time;
    private String key;
    private String value;

    public String getBotId() {
        return botId;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
