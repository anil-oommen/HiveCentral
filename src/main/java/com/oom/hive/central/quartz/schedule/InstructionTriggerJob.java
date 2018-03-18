package com.oom.hive.central.quartz.schedule;


import com.oom.hive.central.connector.MQTTOutboundGateway;
import com.oom.hive.central.mappers.DataModeToServiceModel;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.types.HiveBotDataType;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.repository.model.HiveBotInstruction;
import com.oom.hive.central.service.BotNotificationService;
import com.oom.hive.central.service.BotReportingService;
import org.quartz.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class InstructionTriggerJob implements Job {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(InstructionTriggerJob.class);

    @Autowired
    private BotReportingService reportingService;

    @Autowired
    private BotNotificationService notificationService;



   /* @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println("InstructionTriggerJob" + reportingService.getBot("11111"));

    }

    public void setDataToWrite(String dataToWrite) {
        this.dataToWrite = dataToWrite;
    }*/

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();


        HiveBot hiveBot = reportingService.getBot(dataMap.getString("instr.botId"));
        StringBuffer sBuffInstrInfo = new StringBuffer();
        if(hiveBot!=null){
            hiveBot.getInstructions().forEach(
                    instr -> {
                        if(instr.getInstrId()==dataMap.getLong("instr.botInstrId")){
                            instr.setExecute(true);
                            sBuffInstrInfo.append(instr.getCommand()+":"
                                    + instr.getParams()+":" + instr.getSchedule());
                            notificationService.executeInstruction(hiveBot,instr);

                        }
                    }
            );
            reportingService.saveBot(hiveBot);
        }

        sBuffInstrInfo.append("]");

        logger.info("JOB+ Instruction Trigger Activated \""
                + dataMap.getString("instr.botId")
                + ":" + dataMap.getLong("instr.botInstrId") +"\"[info:" +sBuffInstrInfo.toString());



    }



}
