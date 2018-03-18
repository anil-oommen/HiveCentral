package com.oom.hive.central.mappers;

import com.oom.hive.central.AppSettings;
import com.oom.hive.central.exception.BotDataParseException;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.Instruction;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.repository.model.HiveBotInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class BotClientDataMapper {

    private static final Logger LOG = LoggerFactory.getLogger(BotClientDataMapper.class);



    /*
    public static HiveBotData dumpToJSON(HiveBot hiveBot ,HiveBotData hiveBotData, boolean includeCompleteData)  {

        hiveBotData.setHiveBotId(hiveBot.getBotId());
        hiveBotData.setHiveBotVersion(hiveBot.getBotVersion());
        hiveBotData.setTimestamp(AppSettings.formatDate(hiveBot.getLastHearbeat()));
        //hiveBotData.setEpochTime(hiveBot.getLastHearbeat().getTime());
        hiveBotData.setEnabledFunctions(hiveBot.getEnabledFunctions());


        //hiveBot.

        if(includeCompleteData) {
            //int dataMapSize = 0;
            for (Map.Entry<String, String> entry : hiveBot.getDataMap().entrySet()) {
                hiveBotData.getDataMap().put(entry.getKey(), entry.getValue());
            }
            //dataMapSize++;
            hiveBotData.getDataMap().put("data.map.size",""+hiveBotData.getDataMap().size());

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
            hiveBotData.getDataMap().put("instruction.coll.size",""+hiveBot.getInstructions().size());
        }


        return hiveBotData;
    }*/




    /*private static HiveBot enrichSensorStatusWithEpochTime(HiveBot hiveBot){
        Map<String,String> extMap = new HashMap<String,String>();
        //hiveBot.getDataMap().forEach();

        for (Map.Entry<String, String> entry : hiveBot.getDataMap().entrySet()) {
            if(entry.getKey().endsWith("SensorStatus")){
                extMap.put((entry.getKey()+"Time"), String.valueOf(System.currentTimeMillis()));
            }
        }
        if(extMap.size()>0){
            hiveBot.getDataMap().putAll(extMap);
        }

        return hiveBot;
    }*/

    public static HiveBot enrichFromJSON(HiveBot hiveBot,
                                         HiveBotData jsonBotData,
                                         EnumSet<AppSettings.HiveSaveOperation> saveOperations
    ) throws BotDataParseException
    {

        StringBuffer stringBuffPayload = new StringBuffer();
        stringBuffPayload.append("JSON to OBJ "+ jsonBotData.getHiveBotId() + ":  >   (");


        if(!StringUtils.isEmpty(jsonBotData.getStatus())){
            hiveBot.setStatus(jsonBotData.getStatus());
            stringBuffPayload.append(" status:"+ hiveBot.getStatus());
            //LOG.info("\t+"+ jsonBotData.getHiveBotId() + " status:" + hiveBot.getStatus());
        }


        if(!StringUtils.isEmpty(jsonBotData.getEnabledFunctions())){
            hiveBot.setEnabledFunctions(jsonBotData.getEnabledFunctions());
            stringBuffPayload.append(" enabledFunction:"+ hiveBot.getEnabledFunctions());
            //LOG.info("\t+"+ jsonBotData.getHiveBotId() + " enabledFunction:" + hiveBot.getEnabledFunctions());
        }



        if(!StringUtils.isEmpty(jsonBotData.getHiveBotVersion())){
            hiveBot.setBotVersion(jsonBotData.getHiveBotVersion());
            stringBuffPayload.append(" botVersion:"+ hiveBot.getBotVersion());
            //LOG.info("\t+"+ jsonBotData.getHiveBotId() + " botVersion:" + hiveBot.getBotVersion());
        }




        if(!StringUtils.isEmpty(jsonBotData.getTimestamp())) {
            try {
                hiveBot.setLastHearbeat(
                        AppSettings.parseDate(jsonBotData.getTimestamp())
                );
            } catch (ParseException pEx) {
                BotDataParseException bdPE = new BotDataParseException("Parse Error on HeartBeat" + pEx.getMessage());
                throw bdPE;
            }
        }



        if(saveOperations.contains(AppSettings.HiveSaveOperation.ADD_DATAMAP)){
            hiveBot.getDataMap().putAll(jsonBotData.getDataMap());
        }else if(saveOperations.contains(AppSettings.HiveSaveOperation.SET_DATAMAP)){
            hiveBot.getDataMap().clear();
            hiveBot.getDataMap().putAll(jsonBotData.getDataMap());
        }else if(saveOperations.contains(AppSettings.HiveSaveOperation.CLEAR_DATAMAP)){
            hiveBot.getDataMap().clear();
        }
        int dataMapSize = hiveBot.getDataMap().size();

        if(saveOperations.contains(AppSettings.HiveSaveOperation.ADD_INSTRUCTIONS)){
            jsonBotData.getInstructions()
                    .forEach(
                            instr-> hiveBot.getInstructions()
                                    .remove(new HiveBotInstruction(
                                            instr.getInstrId(),
                                            instr.getCommand(),
                                            instr.getSchedule(),
                                            instr.getParams(), instr.isExecute()
                                    ))
                    );
            jsonBotData.getInstructions()
                    .forEach(
                            instr-> hiveBot.getInstructions()
                                    .add(new HiveBotInstruction(
                                            instr.getInstrId(),
                                            instr.getCommand(),
                                            instr.getSchedule(),
                                            instr.getParams(), instr.isExecute()
                                    ))
                    );

        }else if(saveOperations.contains(AppSettings.HiveSaveOperation.SET_INSTRUCTIONS)){
            hiveBot.getInstructions().clear();
            jsonBotData.getInstructions()
                    .forEach(
                            instr-> hiveBot.getInstructions()
                                    .add(new HiveBotInstruction(
                                            instr.getInstrId(),
                                            instr.getCommand(),
                                            instr.getSchedule(),
                                            instr.getParams(), instr.isExecute()
                                    ))
                    );
        }else if(saveOperations.contains(AppSettings.HiveSaveOperation.CLEAR_INSTRUCTIONS)){
            hiveBot.getInstructions().clear();
        }
        int instructionSize = hiveBot.getInstructions().size();

        if(saveOperations.contains(AppSettings.HiveSaveOperation.BOT_IS_ALIVE)){
            hiveBot.setLastHearbeat(new java.util.Date());
        }

        /*int dataMapSize = 0;
        if(jsonBotData.getDataMap()!=null) {
            //hiveBot.getProperties().re
            hiveBot.getDataMap().putAll(jsonBotData.getDataMap());
            dataMapSize = hiveBot.getDataMap().size();*/

        /* OLD Implemantion for all Event Logging

        if(jsonBotData.getDataMap()!=null) {
            for (Map.Entry<String, String> entry : jsonBotData.getDataMap().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                boolean keyFound = false;
                LOG.warn("\t+"+ jsonBotData.getHiveBotId() + ": DataMap : " + key +":" + value );
                //System.out.println("JSON MAP" + key + "-" +  value);
                for (BotData botData : botClient.getDataSet()) {
                    if (key.equalsIgnoreCase(botData.getDataKey())) {
                        botData.setDataValue(value);
                        botData.keepData = keyFound = true;
                        //System.out.println("DB MAP, Key Exists , Retain" + key + "-" +  value);
                    }
                }
                if (!keyFound) {
                    //System.out.println("DB MAP, New Key  Value  " + key + "-" +  value);
                    BotData newBotData = new BotData(key, value);
                    newBotData.keepData =true;
                    botClient.getDataSet().add(newBotData);
                }
            }
        }

        botClient.getDataSet().removeIf(
                (BotData botData) ->{return !botData.keepData;}
        );
        */

            //System.out.println("DB MAP, Final DataSet to Insert " + botClient.getDataSet().size());
        //}
        stringBuffPayload.append(") dataMapSize:" + dataMapSize + " instructionSize:" + instructionSize);
        LOG.info(stringBuffPayload.toString());
        return hiveBot;
    }


}
