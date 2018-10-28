package com.oom.hive.central.repository.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oom.hive.central.AppSettings;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;

import java.text.SimpleDateFormat;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HiveBotEvent {

    public HiveBotEvent(){}

    private HiveBotEvent(String eventId, String botId, Date time){
        this.eventId = eventId;
        this.botId = botId;
        this.time = time;
    }

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    public static HiveBotEvent newInstance(String botId,
                                           Date eventTime,
                                           String key){
        return new HiveBotEvent(
        sdf.format(eventTime) + "." +
                StringUtils.right(DigestUtils.md5Hex(botId).toUpperCase(),3) +"." +
                StringUtils.right(DigestUtils.md5Hex(key).toUpperCase(),3)
                ,botId,eventTime);
    }

    @Id
    private String eventId;

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

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
