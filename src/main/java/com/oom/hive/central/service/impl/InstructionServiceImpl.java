package com.oom.hive.central.service.impl;

import com.oom.hive.central.AppSettings;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.quartz.schedule.InstructionTriggerJob;
import com.oom.hive.central.quartz.schedule.JobScheduleModel;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.service.InstructionService;
import org.quartz.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InstructionServiceImpl implements InstructionService{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(InstructionServiceImpl.class);

    public List<JobScheduleModel> getNewJSModelToInitilize(HiveBotData jsonBotData, EnumSet<AppSettings.HiveSaveOperation> saveOperations){
        List<JobScheduleModel> jsm = new ArrayList<JobScheduleModel>();
        if(saveOperations.contains(AppSettings.HiveSaveOperation.ADD_INSTRUCTIONS) ||
                saveOperations.contains(AppSettings.HiveSaveOperation.SET_INSTRUCTIONS)){
            jsonBotData.getInstructions()
                    .forEach(
                            instr-> {
                                JobScheduleModel jobScheduleModel = _buildJSModelIfValid(jsonBotData.getHiveBotId(),
                                        instr.getInstrId(),
                                        instr.getSchedule(),
                                        instr.getCommand(),
                                        instr.getParams()
                                );
                                if(jobScheduleModel!=null) {
                                    jsm.add(jobScheduleModel);
                                    logger.info("           --Register Incoming JobScheduleModel (" +
                                                    jobScheduleModel.getJobDetail().getKey().getName() +")");
                                }
                            }
                    );
        }
        return jsm;
    }


    public List<JobScheduleModel> getAllJobModelToInitilize(HiveBot hiveBot){
        List<JobScheduleModel> jsm = new ArrayList<JobScheduleModel>();
        hiveBot.getInstructions()
                .forEach(
                        instr-> {

                            JobScheduleModel jobScheduleModel = _buildJSModelIfValid(hiveBot.getBotId(),
                                    instr.getInstrId(),
                                    instr.getSchedule(),
                                    instr.getCommand(),
                                    instr.getParams()
                            );
                            if(jobScheduleModel!=null) {
                                jsm.add(jobScheduleModel);
                                logger.info("           --Register JobScheduleModel (" +
                                        jobScheduleModel.getJobDetail().getKey().getName() +")");
                            }



                        }
                );

        return jsm;

    }




    private JobScheduleModel _buildJSModelIfValid(String hiveBotId, long botInstrId, String instrSchedule, String command, String params) {
        //.usingJobData("param.botId", botId)
        //       .usingJobData("param.botInstrId", botInstrId)
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("instr.botId", hiveBotId);
        jobDataMap.put("instr.botInstrId", botInstrId);
        jobDataMap.put("instr.command", command);
        jobDataMap.put("instr.params", params);
        Trigger trigger = _buildTrigger(hiveBotId, botInstrId, instrSchedule);

        if (trigger != null) {
            JobDetail jobDetail = JobBuilder.newJob(InstructionTriggerJob.class)
                    .setJobData(jobDataMap)
                    .withDescription("HiveBot Instruction Schedulers")
                    .withIdentity(_assembleJobKey(hiveBotId,botInstrId))
                    .build();


            JobScheduleModel jobScheduleModel = new JobScheduleModel(jobDetail, trigger);
            return jobScheduleModel;
        }
        return null;
    }

    public boolean isMatchingBot(JobKey jobKey, String hiveBotId){
        return jobKey.getName().startsWith( hiveBotId+"_" );
    }

    public static String HIVEBOT_GROUP_NAME = "HiveBotGroup";



    private JobKey _assembleJobKey(String hiveBotId, long botInstrId){
        JobKey jobKeyB = new JobKey( hiveBotId+"_" + botInstrId, HIVEBOT_GROUP_NAME);
        return jobKeyB;
    }

    private Trigger _buildTrigger(String botId, long botInstrId, String botSchedule){
        if(StringUtils.isEmpty(botSchedule)) return null;
        try {
            if (botSchedule.startsWith("runonce.now:")) {
                Trigger trigger = TriggerBuilder.newTrigger()
                        .startNow()
                        .build();
                return trigger;
            } else if (botSchedule.startsWith("schedule.cron:")) {
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withSchedule(CronScheduleBuilder.cronSchedule(
                                botSchedule.substring(14))
                        )
                        .build();
                //System.err.println("++_+_:" + botSchedule.substring(14));
                return trigger;
            } else if (botSchedule.startsWith("schedule.daily.HHMM:")) {
                int hour = Integer.parseInt(botSchedule.substring(20, 20 + 2));
                int minute = Integer.parseInt(botSchedule.substring(22, 22 + 2));

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withSchedule(
                                CronScheduleBuilder.dailyAtHourAndMinute(hour, minute))
                        .build();
                return trigger;
            }else{
                logger.error("           --Invalid Schedule Expression " +
                        botId + ":" + botInstrId +" " +
                        botSchedule);
            }
        }catch(RuntimeException rEx){
            logger.error("           --Error Parsing Schedule Expression " +
                    botId + ":" + botInstrId +" " +
                    botSchedule +" " + rEx.getMessage() +" Ignoring.");
        }
        return null;
    }

}
