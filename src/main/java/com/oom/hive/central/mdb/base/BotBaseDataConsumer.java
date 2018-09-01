package com.oom.hive.central.mdb.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oom.hive.central.AppSettings;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.types.HiveBotDataType;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.service.BotReportingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.EnumSet;

public abstract class BotBaseDataConsumer implements BaseConsumer {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BotBaseDataConsumer.class);

    @Autowired
    BotReportingService reportingService;

    ObjectMapper objectMapper = new ObjectMapper();

    public abstract void postSaveSensorData(HiveBot botData);

    public abstract void postBotBootup(HiveBot botData);

    public abstract void postBotInstructionCompleted(HiveBot botData);

    public abstract void postBotInstructionFailed(HiveBot botData);

    public abstract void heartbeat(HiveBot botData);

    @Override
    public void handleMessage(String messageData) {
        HiveBotData botData = null;

        try {
            botData = objectMapper.readValue(messageData,
                    HiveBotData.class);

            //Authenticate Bot
            if (!StringUtils.isEmpty(botData.getHiveBotId())
                    && reportingService.authenticate(botData.getHiveBotId(), botData.getAccessKey())
                ) {
                if (HiveBotDataType.SENSOR_DATA.equals(botData.getDataType())) {
                    EnumSet<AppSettings.HiveSaveOperation> saveOperations =
                            EnumSet.of(AppSettings.HiveSaveOperation.SAVE_INFO,
                                    AppSettings.HiveSaveOperation.ADD_DATAMAP,
                                    AppSettings.HiveSaveOperation.EVENTLOG_DATAMAP,
                                    AppSettings.HiveSaveOperation.BOT_IS_ALIVE
                            );
                    HiveBot hiveBot = reportingService.saveBot(botData, saveOperations);
                    postSaveSensorData(hiveBot);
                } else if (HiveBotDataType.BOOTUP_HIVEBOT.equals(botData.getDataType())) {
                    postBotBootup(reportingService.getBot(botData.getHiveBotId()));
                } else if (HiveBotDataType.INSTRUCTION_COMPLETED.equals(botData.getDataType())){
                    pMarkInstructionStstus(botData,"Completed Sucessfull");
                    postBotInstructionCompleted(reportingService.getBot(botData.getHiveBotId()));
                } else if (HiveBotDataType.INSTRUCTION_FAILED.equals(botData.getDataType())){
                    pMarkInstructionStstus(botData,"Completed Failed.");
                    postBotInstructionFailed(reportingService.getBot(botData.getHiveBotId()));
                } else if (HiveBotDataType.HEART_BEAT.equals(botData.getDataType())){
                    heartbeat(reportingService.getBot(botData.getHiveBotId()));
                } else {
                    logger.warn("Unsupported or Null DataType Ignoring: {}", botData.getDataType());
                }

            } else {
                logger.warn("Authentication Failed with Creds {} {} ", botData.getHiveBotId(), botData.getAccessKey());
            }
        }catch (IOException ioE){
            logger.error("Error Parsing Message", ioE);
        }
    }

    private void pMarkInstructionStstus(HiveBotData botData, String result){
        botData.getInstructions().forEach(insr->{
            reportingService.markInstructionCompleted(
                    botData,
                    insr.getInstrId(),
                    insr.getCommand(),
                    result,
                    EnumSet.of(AppSettings.HiveSaveOperation.SAVE_INFO,
                            AppSettings.HiveSaveOperation.BOT_IS_ALIVE
                    )
            );
        });
    }
}
