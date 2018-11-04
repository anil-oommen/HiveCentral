package com.oom.hive.central.model;

public class Instruction {
    long instrId;
    String command;
    String schedule;
    String params;
    boolean execute;

    public Instruction(){}

    public Instruction(long instrId, String command, String schedule, String params, boolean execute){
        this.instrId = instrId;
        this.command = command;
        this.schedule = schedule;
        this.params = params;
        this.execute = execute;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }


    public long getInstrId() {
        return instrId;
    }

    public void setInstrId(long instrId) {
        this.instrId = instrId;
    }

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }
}
