package com.oom.hive.central.model.job;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.oom.hive.central.AppSettings;

import java.util.Date;

public class InstructionJobSchedule {
    String key;
    String group;
    String command;
    String params;

    @JsonFormat(pattern= AppSettings.TSTAMP_FORMAT,timezone = "Asia/Singapore",
            locale = "en_GB")
    Date nextFireTime;
    int priority;
    boolean paused;
    int triggerSize;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Date getNextFireTime() {
        return nextFireTime;
    }

    public void setNextFireTime(Date nextFireTime) {
        this.nextFireTime = nextFireTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getTriggerSize() {
        return triggerSize;
    }

    public void setTriggerSize(int triggerSize) {
        this.triggerSize = triggerSize;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }


}
