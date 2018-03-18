package com.oom.hive.central.repository.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HiveBotInstruction {

    @Id
    long instrId;
    String command;
    String schedule;
    boolean execute;
    String params;

    public HiveBotInstruction(){}
    public HiveBotInstruction(long instrId,String command, String schedule, String params,
            boolean execute)
    {
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HiveBotInstruction that = (HiveBotInstruction) o;
        return instrId == that.instrId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(instrId);
    }
}
