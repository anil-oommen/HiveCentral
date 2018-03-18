package com.oom.hive.central.service;

import com.oom.hive.central.AppSettings;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.quartz.schedule.JobScheduleModel;
import com.oom.hive.central.repository.model.HiveBot;
import org.quartz.JobKey;

import java.util.EnumSet;
import java.util.List;

public interface InstructionService {

    List<JobScheduleModel> getAllJobModelToInitilize(HiveBot hiveBot);
    List<JobScheduleModel> getNewJSModelToInitilize(HiveBotData jsonBotData, EnumSet<AppSettings.HiveSaveOperation> saveOperations);
    boolean isMatchingBot(JobKey jobKey, String hiveBotId);
}
