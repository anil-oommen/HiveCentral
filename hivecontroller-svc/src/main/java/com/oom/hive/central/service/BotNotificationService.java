package com.oom.hive.central.service;

import com.oom.hive.central.mappers.DataModeToServiceModel;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.types.HiveBotDataType;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.repository.model.HiveBotInstruction;

public interface BotNotificationService {

    void executeInstruction(HiveBot hiveBot, HiveBotInstruction hiveBotInstruction);
    void updateFunctions(HiveBot hiveBot);
    void sendCatchupForBotClient(HiveBot hiveBot);
}
