package com.oom.hive.central.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oom.hive.central.AppSettings;
import com.oom.hive.central.exception.BotDataParseException;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.types.HiveBotDataType;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.service.BotNotificationService;
import com.oom.hive.central.service.BotReportingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.EnumSet;

@Component
public class MQTTInboundHandler implements MessageHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MQTTInboundHandler.class);

    @Autowired
    BotReportingService reportingService;

    @Autowired
    BotNotificationService notificationService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleMessage(Message<?> message) {
        try {
            StringBuilder sBuffer = new StringBuilder();
            message.getHeaders().forEach((key, value) ->
                sBuffer.append(" " + key + " = " + value)
            );
            logger.info("MQTT Received  <  <  < ");
            logger.info("MQTT < Message Header({})", sBuffer);
            logger.info("MQTT < Payload ({})", message.getPayload().toString());

            HiveBotData botData = null;


            botData = objectMapper.readValue(message.getPayload().toString(),
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

                    //Take note Bot is alive, might not be sending heartBeat if in DeepSleep.
                    EnumSet<AppSettings.HiveSaveOperation> saveOperations =
                            EnumSet.of(
                                    AppSettings.HiveSaveOperation.BOT_IS_ALIVE
                            );
                    reportingService.saveBot(botData, saveOperations);
                } else if (HiveBotDataType.INSTRUCTION_COMPLETED.equals(botData.getDataType())
                        || HiveBotDataType.INSTRUCTION_FAILED.equals(botData.getDataType())
                        ) {
                    pMarkInstructionCompleted(botData);

                } else if (HiveBotDataType.HEART_BEAT.equals(botData.getDataType())) {
                    EnumSet<AppSettings.HiveSaveOperation> saveOperations =
                            EnumSet.of(
                                    AppSettings.HiveSaveOperation.BOT_IS_ALIVE
                            );
                    reportingService.saveBot(botData, saveOperations);
                } else {
                    logger.warn("Unsupported or Null DataType Ignoring: {}", botData.getDataType());
                }

            } else {
                logger.warn("Authentication Failed with Creds {} {} ", botData.getHiveBotId(), botData.getAccessKey());
            }

        } catch (BotDataParseException bEx) {
            logger.warn("BadRequest Payload Ignoring: {} ", message.getPayload());
        }catch (IOException ioE){
            logger.error("MQTT JSON ParseError ", ioE);
        }catch(Exception rEx){
            logger.error("Error Handling Message", rEx);
            throw rEx;
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
