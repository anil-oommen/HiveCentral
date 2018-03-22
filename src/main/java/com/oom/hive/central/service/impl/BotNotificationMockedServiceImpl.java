package com.oom.hive.central.service.impl;

import com.oom.hive.central.connector.MQTTOutboundGateway;
import com.oom.hive.central.mappers.DataModeToServiceModel;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.Instruction;
import com.oom.hive.central.model.types.HiveBotDataType;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.repository.model.HiveBotInstruction;
import com.oom.hive.central.service.BotNotificationService;
import com.oom.hive.central.utils.JsonLogHelper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"!ModuleMQTT"})
public class BotNotificationMockedServiceImpl implements BotNotificationService{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BotNotificationMockedServiceImpl.class);


    public void executeInstruction(HiveBot hiveBot, HiveBotInstruction hiveBotInstruction){
        logger.warn("Disabled @See Profile Selected. No action for 'executeInstruction'");
    }

    public void updateFunctions(HiveBot hiveBot){
        logger.warn("Disabled @See Profile Selected. No action for 'updateFunctions'");
    }

    public void sendCatchupForBotClient(HiveBot hiveBot){
        logger.warn("Disabled @See Profile Selected. No action for 'sendCatchupForBotClient'");
    }
}
