package com.oom.hive.central.mdb;


import com.oom.hive.central.AppSettings;
import com.oom.hive.central.mdb.base.BotBaseDataConsumer;
import com.oom.hive.central.mdb.base.ConsumerSubQueue;
import com.oom.hive.central.model.types.HiveBotDataType;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.service.BotNotificationService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@ConsumerSubQueue({"microclimate"})
@Component
@Profile({"ModuleMQTT"})
public class IOTBotDataConsumer extends BotBaseDataConsumer {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(IOTBotDataConsumer.class);

    @Autowired
    BotNotificationService notificationService;

    @Override
    public void postSaveSensorData(HiveBot botData){
        //No Specific Action.
    }


    @Override
    public void postBotBootup(HiveBot botData){
        //At Bootup , Send CatchUp for all Missed Work.
        //Note , while in DeepSleep all MQTT messages are missed.
        notificationService.sendCatchupForBotClient(botData);
    }

    @Override
    public void postBotInstructionCompleted(HiveBot botData){

    }

    @Override
    public void postBotInstructionFailed(HiveBot botData){
        //No Specific Action.
    }

    @Override
    public void heartbeat(HiveBot botData){
        //No Specific Action
    }
}
