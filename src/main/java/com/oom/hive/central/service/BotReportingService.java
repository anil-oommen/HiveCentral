package com.oom.hive.central.service;

import com.oom.hive.central.AppSettings;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.repository.model.HiveBotEvent;

import java.util.EnumSet;
import java.util.List;


public interface BotReportingService {

    Iterable<HiveBot> listAllHiveBots();

    String register(String botId, String botVersion);
    boolean authenticate(String botId, String botAccessKey);
    HiveBot getBot(String botId);
    HiveBot saveBot(HiveBotData botData, EnumSet<AppSettings.HiveSaveOperation> saveOperations);
    HiveBot markInstructionCompleted(HiveBotData botData,
                                     long instrId,
                                     String command,
                                     String result,
                                     EnumSet<AppSettings.HiveSaveOperation> saveOperations
    );
    void saveBot(HiveBot hiveBot);
}
