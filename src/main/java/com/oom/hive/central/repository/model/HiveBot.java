package com.oom.hive.central.repository.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.oom.hive.central.AppSettings;
import org.springframework.data.annotation.Id;

import javax.persistence.*;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HiveBot {


    /*@Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    */

    //@Column(unique=true)
    @Id
    public String botId;
    public String botVersion;

    @JsonFormat(pattern= AppSettings.TSTAMP_FORMAT)
    public Date lastHearbeat;
    public String status;

    String enabledFunctions;


    Map<String,String> dataMap = new HashMap<String,String>();
    Collection<HiveBotInstruction> instructions = new HashSet<HiveBotInstruction>();


    public String toString(){
        return  "(" + botId +"." + botVersion + ")= " + status + " @ " + lastHearbeat;
    }

    public String getBotId() {
        return botId;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }

    public String getBotVersion() {
        return botVersion;
    }

    public void setBotVersion(String botVersion) {
        this.botVersion = botVersion;
    }

    public Date getLastHearbeat() {
        return lastHearbeat;
    }

    public void setLastHearbeat(Date lastHearbeat) {
        this.lastHearbeat = lastHearbeat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, String> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, String> dataMap) {
        this.dataMap = dataMap;
    }

    public String getEnabledFunctions() {
        return enabledFunctions;
    }

    public void setEnabledFunctions(String enabledFunctions) {
        this.enabledFunctions = enabledFunctions;
    }

    public Collection<HiveBotInstruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(Collection<HiveBotInstruction> instructions) {
        this.instructions = instructions;
    }
}
