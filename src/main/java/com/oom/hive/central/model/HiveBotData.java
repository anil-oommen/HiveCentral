package com.oom.hive.central.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

public class HiveBotData {

    String dataType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String,String> dataMap = new HashMap<>();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Collection<Instruction> instructions = new ArrayList<Instruction>();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String enabledFunctions;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String hiveBotVersion;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String accessKey;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Long secondsSinceLastBotPulse;


/*    long epochTime;*/

    String hiveBotId;

    public String getHiveBotId() {
        return hiveBotId;
    }

    public void setHiveBotId(String hiveBotId) {
        this.hiveBotId = hiveBotId;
    }

    public String getHiveBotVersion() {
        return hiveBotVersion;
    }

    public void setHiveBotVersion(String hiveBotVersion) {
        this.hiveBotVersion = hiveBotVersion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEnabledFunctions() {
        return enabledFunctions;
    }

    public void setEnabledFunctions(String enabledFunctions) {
        this.enabledFunctions = enabledFunctions;
    }


    public Collection<Instruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(Collection<Instruction> instructions) {
        this.instructions = instructions;
    }

/*
    public long getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(long epochTime) {
        this.epochTime = epochTime;
    }
*/

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Long getSecondsSinceLastBotPulse() {
        return secondsSinceLastBotPulse;
    }

    public void setSecondsSinceLastBotPulse(Long secondsSinceLastBotPulse) {
        this.secondsSinceLastBotPulse = secondsSinceLastBotPulse;
    }
}
