package com.oom.hive.central.model;

import com.oom.hive.central.repository.model.HiveBot;

public class HiveCentralResponse {
    private String ackSuccess;
    private String message;
    private Iterable<HiveBot> bots;

    public HiveCentralResponse(String ackSuccess, String message){
        this.ackSuccess = ackSuccess;
        this.message = message;
    }

    public String getAckSuccess() {
        return ackSuccess;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAckSuccess(String ackSuccess) {
        this.ackSuccess = ackSuccess;
    }


    public Iterable<HiveBot> getBots() {
        return bots;
    }

    public void setBots(Iterable<HiveBot> bots) {
        this.bots = bots;
    }
}
