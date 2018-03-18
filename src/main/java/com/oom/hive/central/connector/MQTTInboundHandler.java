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

    /* Sample
    *  http://localhost:8080/hivemq-mqtt-web-client/index.html
    *   hivecentral/iot/exchange/client
    *----------------------
        {
          "accessKey": "3cfe3256ba8b7a54b464370c68f59b6352d9907979bb8ab037e5da9f0ff7a23d",
          "dataMap": {
             "source":"JSONMQTTTEST"
           },
          "hiveBotId": "OOMM.HIVE MICLIM.01"
        }
    *----------------------
    *
    */

    @Autowired
    BotReportingService reportingService;

    @Autowired
    BotNotificationService notificationService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        try {
            StringBuffer sBuffer = new StringBuffer();
            message.getHeaders().forEach((key, value) -> {
                sBuffer.append(" " + key + " = " + value);
            });
            logger.info("MQTT Received  <  <  < ");
            logger.info("MQTT < Message Header(" + sBuffer.toString() + ")");
            logger.info("MQTT < Payload (" + message.getPayload().toString() + ")");

            //if (1 > 0) return;

            HiveBotData botData = null;
            try {
                botData = objectMapper.readValue(message.getPayload().toString(),
                        HiveBotData.class);
            } catch (IOException e) {
                logger.error("MQTT JSON ParseError ", e);
                return;
            }
            //botData.getHiveBotId();

            //HiveBotData responseBotData = new HiveBotData();
            //responseBotData.setHiveBotId(botData.getHiveBotId());
            //responseBotData.setHiveBotVersion(botData.getHiveBotVersion());

            try {
                //Authenticate Bot
                if (!StringUtils.isEmpty(botData.getHiveBotId())
                        && reportingService.authenticate(botData.getHiveBotId(), botData.getAccessKey())
                        ) {
                    if(HiveBotDataType.SensorData.equals(botData.getDataType())) {
                        EnumSet<AppSettings.HiveSaveOperation> saveOperations =
                                EnumSet.of(AppSettings.HiveSaveOperation.SAVE_INFO,
                                        AppSettings.HiveSaveOperation.ADD_DATAMAP,
                                        AppSettings.HiveSaveOperation.EVENTLOG_DATAMAP,
                                        AppSettings.HiveSaveOperation.BOT_IS_ALIVE
                                );
                        reportingService.saveBot(botData, saveOperations);

                    } else if(HiveBotDataType.BootupHivebot.equals(botData.getDataType())) {
                        //At Bootup , Send CatchUp for all Missed Work.
                        //Note , while in DeepSleep all MQTT messages are missed.
                        notificationService.sendCatchupForBotClient(reportingService.getBot(botData.getHiveBotId()));
                    }else if(HiveBotDataType.InstructionCompleted.equals(botData.getDataType())
                            || HiveBotDataType.InstructionFailed.equals(botData.getDataType())
                            ){
                        _markInstructionCompleted(botData);

                    }else{
                        logger.warn("Unsupported or Null DataType Ignoring: " + botData.getDataType());
                    }

                } else {
                    logger.warn("Authentication Failed with Creds" + botData.getHiveBotId() + " " + botData.getAccessKey());
                }
            } catch (BotDataParseException bEx) {
                logger.warn("BadRequest Payload Ignoring:" + message.getPayload());
            }
        }catch(Exception rEx){
            logger.error("Error Handling Message", rEx);
            throw rEx;
        }
    }

    private void _markInstructionCompleted(HiveBotData botData){
        botData.getInstructions().forEach(insr->{
            HiveBot hiveBot = reportingService.markInstructionCompleted(
                    botData,
                    insr.getInstrId(),
                    insr.getCommand(),
                    (HiveBotDataType.InstructionCompleted.equals(botData.getDataType())?"Completed Sucessfull":"Completed Failed."),
                    EnumSet.of(AppSettings.HiveSaveOperation.SAVE_INFO,
                            AppSettings.HiveSaveOperation.BOT_IS_ALIVE
                            )
            );
        });
    }
}
