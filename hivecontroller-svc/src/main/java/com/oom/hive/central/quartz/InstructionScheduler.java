package com.oom.hive.central.quartz;

import com.oom.hive.central.AppSettings;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.job.InstructionJobSchedule;
import com.oom.hive.central.quartz.schedule.JobScheduleModel;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.service.BotReportingService;
import com.oom.hive.central.service.InstructionService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class InstructionScheduler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(InstructionScheduler.class);

    private SchedulerFactoryBean schedulerFactoryBean;


    @Autowired
    BotReportingService botReportingService;
    @Autowired
    InstructionService instructionService;

    final int QUARTZ_STARTUP_DELAY_SECONDS = 10;

    @Autowired
    public InstructionScheduler(SchedulerFactoryBean schedulerFactoryBean
            ) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    @PostConstruct
    public void init() {
        logger.info("    +++++++ initializing Quartz Schedule @Context Refresh "
                +"------------------------ ");
        registerInstructionInStore();

        try {
            logger.info("           -Starting Quartz Scheduler Delay.Seconds({})",QUARTZ_STARTUP_DELAY_SECONDS);
            schedulerFactoryBean.getScheduler().startDelayed(QUARTZ_STARTUP_DELAY_SECONDS);
        } catch (SchedulerException e) {
            logger.error("           -Error Starting  Quartz Scheduler ",e);
        }
    }

    private void _removeAllScheduledJobs(HiveBotData jsonBotData){
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        try {
            logger.info("           -Scanning Existing Schedule to Delete/Deactivate(SET ) :   ");
            scheduler.getJobKeys(GroupMatcher.anyGroup()).forEach(jobKey-> {
                if(instructionService.isMatchingBot(jobKey,jsonBotData.getHiveBotId())){
                    logger.info("           --Found a Matching Schedule ({}). Deleting ",jobKey.getName());
                    try {
                        scheduler.deleteJob(jobKey);
                    } catch (SchedulerException e) {
                        logger.error("           --Error Deleting Old Scheduler ",e);
                    }
                }
            });
        } catch (SchedulerException e) {
            logger.error("           -Error Scanning for Old Jobs of HiveBot to delete ", e);
        }
    }

    public boolean registerNewInboundInstruction(HiveBotData jsonBotData,
                                                 EnumSet<AppSettings.HiveSaveOperation> saveOperations
    ){


        if(saveOperations.contains(AppSettings.HiveSaveOperation.SET_INSTRUCTIONS)){
            _removeAllScheduledJobs(jsonBotData);
        }

        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        logger.info("           -Scanning For New Schedule To Register in Request :  {}",jsonBotData.getHiveBotId());
        List<JobScheduleModel> jobScheduleModels = instructionService.getNewJSModelToInitilize(jsonBotData,saveOperations);
        jobScheduleModels.forEach(jsModel-> {
            try {
                scheduler.getJobKeys(GroupMatcher.anyGroup()).forEach(jobKey->{
                    if(jsModel.getJobDetail().getKey().getName().equals(jobKey.getName())){
                        logger.info("           --Check JobKey already Exists :  Found:({}) ",jsModel.getJobDetail().getKey().getName());
                        try {
                            scheduler.deleteJob(jobKey);
                            logger.info("           --Old Job Deleted:" );
                        } catch (SchedulerException e) {
                            logger.error("           --Error Deleting old Job ({})",jobKey.getName(),e);
                        }
                    }
                });

                scheduler.scheduleJob(jsModel.getJobDetail(), jsModel.getTrigger());
            } catch (SchedulerException e) {
                logger.error("           --Error Scheduling {} in : {}  " ,
                        jobScheduleModels.size(),
                        jsonBotData.getHiveBotId() ,e);
            }
        });

        logger.info("           -Found & Scheduled {} in :  {} " ,
                jobScheduleModels.size(),
                jsonBotData.getHiveBotId() );
        return jobScheduleModels.size()>0;
    }

    public void registerInstructionInStore() {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        Iterable<HiveBot> hiveBots = botReportingService.listAllHiveBots();


        hiveBots.forEach(hiveBot ->{
            logger.info("           -Scanning For Schedule in :  {} ",hiveBot.getBotId());
            List<JobScheduleModel> jobScheduleModels=
                    instructionService.getAllJobModelToInitilize(
                    hiveBot
            );

            jobScheduleModels.forEach(jsModel-> {
                try {
                    scheduler.scheduleJob(jsModel.getJobDetail(), jsModel.getTrigger());
                } catch (SchedulerException e) {
                    logger.error("           --Error Scheduling ({}) in : {}  " ,
                            jsModel.getJobDetail().getKey().getName(),
                            hiveBot.getBotId() ,e);
                }
            });

            logger.info("           -Found & Scheduled {} in :  {} " ,
                    jobScheduleModels.size(),
                    hiveBot.getBotId() );

        });


    }

    public boolean removeScheduleByKey(String hiveBotId, String instructionJobKey){
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.getJobKeys(GroupMatcher.anyGroup()).forEach(jobKey-> {
                try {
                    if(jobKey.getName().equals(instructionJobKey)){
                        //Found the one to Delete.
                        scheduler.deleteJob(jobKey);
                        logger.info("           -Removed Scheduled Instruction Job  : {}"  , instructionJobKey);
                    }
                } catch (SchedulerException e) {
                    logger.error("           --Error Gathering all Job & Trigger Details :  " , e);
                }
            });

            HiveBot hiveBot = botReportingService.getBot(hiveBotId);

            if(hiveBot!=null){
                int originalSize =hiveBot.getInstructions().size();
                hiveBot.getInstructions().removeIf(
                        instr-> instructionJobKey.endsWith(String.valueOf(instr.getInstrId())));
                if(originalSize> hiveBot.getInstructions().size()){
                    botReportingService.saveBot(hiveBot);
                    logger.info("           --Removed HiveBot Instruction from DB: {}"  , instructionJobKey);
                }else{
                    logger.error("           --Could not remove HiveBot Instruction from DB, Instruction Not Found: {}"  , instructionJobKey);
                    return false;
                }
            }else{
                logger.error("           --HiveBot Not found ! Ignoring your delete instruction. {}" , instructionJobKey);
                return false;
            }


        } catch (SchedulerException e) {
            logger.error("           -Error Gathering all Jobs to Print :  " );
        }
        return true;
    }


    public List<InstructionJobSchedule> getScheduledJobs() {
        List<InstructionJobSchedule> instructionJobSchedules = new ArrayList<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.getJobKeys(GroupMatcher.anyGroup()).forEach(jobKey-> {

                try {
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    final List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                    Date nextExecution = null;
                    int priority = 0;
                    if (!triggers.isEmpty())
                    {
                        nextExecution = triggers.get(0).getNextFireTime();
                        priority = triggers.get(0).getPriority();
                    }
                    InstructionJobSchedule insJob = new InstructionJobSchedule();
                    insJob.setKey(jobKey.getName());
                    insJob.setGroup(jobKey.getGroup());
                    insJob.setNextFireTime(nextExecution);
                    insJob.setPriority(priority);
                    insJob.setPaused((isJobPaused(jobKey)));
                    insJob.setTriggerSize(triggers.size());

                    insJob.setCommand(jobDetail.getJobDataMap().getString("instr.command"));
                    insJob.setParams(jobDetail.getJobDataMap().getString("instr.params"));
                    instructionJobSchedules.add(insJob);
                } catch (SchedulerException e) {
                    logger.error("           --Error Gathering all Job & Trigger Details :  " , e);
                }
            });

            Collections.sort(instructionJobSchedules,insrJobComparator);
        } catch (SchedulerException e) {
            logger.error("           -Error Gathering all Jobs to Print :  " );
        }

        return instructionJobSchedules;
    }


    public static final Comparator<InstructionJobSchedule> insrJobComparator =
    (InstructionJobSchedule s1,InstructionJobSchedule s2)-> {
        Date jobSchedule1 = s1.getNextFireTime();
        Date jobSchedule2 = s2.getNextFireTime();
        if(jobSchedule1!=null && jobSchedule2!=null){
            return jobSchedule1.compareTo(jobSchedule2);
        }else{
            return s1.getKey().compareTo(s2.getKey());
        }
    };



    private Boolean isJobPaused(JobKey jobKey) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
        for (Trigger trigger : triggers) {
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            if (Trigger.TriggerState.PAUSED.equals(triggerState)) {
                return true;
            }
        }
        return false;
    }
}
