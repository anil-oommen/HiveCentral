package com.oom.hive.central.mappers;

import com.oom.hive.central.AppSettings;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.Instruction;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.repository.model.HiveBotInstruction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class DataModeToServiceModel {

    public static HiveBotData buildBasicJsonData(HiveBot hiveBot )  {
        HiveBotData hiveBotData = new HiveBotData();
        hiveBotData.setHiveBotId(hiveBot.getBotId());
        hiveBotData.setHiveBotVersion(hiveBot.getBotVersion());
        hiveBotData.setTimestamp(new SimpleDateFormat(AppSettings.TSTAMP_FORMAT).
                format((hiveBot.getLastHearbeat())));
        return hiveBotData;
    }

    public static HiveBotData enrichAliveInfo(HiveBot hiveBot ,HiveBotData hiveBotData){
        hiveBotData.setSecondsSinceLastBotPulse((System.currentTimeMillis() - hiveBot.getLastHearbeat().getTime())/1000);
        return hiveBotData;
    }

    public static HiveBotData enrichFunctions(HiveBot hiveBot ,HiveBotData hiveBotData){
        hiveBotData.setEnabledFunctions(hiveBot.getEnabledFunctions());
        return hiveBotData;
    }

    public static HiveBotData enrichFullDataMap(HiveBot hiveBot ,HiveBotData hiveBotData){
        for (Map.Entry<String, String> entry : hiveBot.getDataMap().entrySet()) {
            hiveBotData.getDataMap().put(entry.getKey(), entry.getValue());
        }
        return hiveBotData;
    }

    public static HiveBotData enrichFullInstructions(HiveBot hiveBot ,HiveBotData hiveBotData){
        hiveBot.getInstructions()
                .forEach(
                        instr-> hiveBotData.getInstructions()
                                .add(new Instruction(
                                        instr.getInstrId(),
                                        instr.getCommand(),
                                        instr.getSchedule(),
                                        instr.getParams(), instr.isExecute()
                                ))
                );
        return hiveBotData;
    }

}
