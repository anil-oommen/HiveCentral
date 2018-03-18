package com.oom.hive.central.quartz.schedule;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class JobScheduleModel {
    private JobDetail jobDetail;
    private Trigger trigger;

    public JobScheduleModel(JobDetail jobDetail, Trigger trigger) {
        this.jobDetail = jobDetail;
        this.trigger = trigger;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public Trigger getTrigger() {
        return trigger;
    }
}
