package com.oom.hive.central.mdb;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.oom.hive.central.AppSettings;
import com.oom.hive.central.mdb.base.BaseConsumer;
import com.oom.hive.central.mdb.base.ConsumerSubQueue;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.types.HiveBotDataType;
import com.oom.hive.central.service.BotNotificationService;
import com.oom.hive.central.service.BotReportingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.EnumSet;

@ConsumerSubQueue("deleteall")
@Component
@Profile({"ModuleMQTT"})
@Deprecated
public class EventNotificationConsumer implements BaseConsumer {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EventNotificationConsumer.class);


    @Autowired
    BotReportingService reportingService;

    @Autowired
    BotNotificationService notificationService;

    ObjectMapper objectMapper = new ObjectMapper();

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
                    reportingService.saveBot(botData, saveOperations);

                } else if (HiveBotDataType.BOOTUP_HIVEBOT.equals(botData.getDataType())) {
                    //At Bootup , Send CatchUp for all Missed Work.
                    //Note , while in DeepSleep all MQTT messages are missed.
                    notificationService.sendCatchupForBotClient(reportingService.getBot(botData.getHiveBotId()));
                } else if (HiveBotDataType.INSTRUCTION_COMPLETED.equals(botData.getDataType())
                        || HiveBotDataType.INSTRUCTION_FAILED.equals(botData.getDataType())
                        ) {
                    pMarkInstructionCompleted(botData);

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

    private void pMarkInstructionCompleted(HiveBotData botData){
        botData.getInstructions().forEach(insr->{
            reportingService.markInstructionCompleted(
                    botData,
                    insr.getInstrId(),
                    insr.getCommand(),
                    (HiveBotDataType.INSTRUCTION_COMPLETED.equals(botData.getDataType())?"Completed Sucessfull":"Completed Failed."),
                    EnumSet.of(AppSettings.HiveSaveOperation.SAVE_INFO,
                            AppSettings.HiveSaveOperation.BOT_IS_ALIVE
                    )
            );
        });
    }
}
