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
import java.text.SimpleDateFormat;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class BotClientDataMapper {

    private static final Logger LOG = LoggerFactory.getLogger(BotClientDataMapper.class);




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
                        new SimpleDateFormat(AppSettings.TSTAMP_FORMAT).parse(jsonBotData.getTimestamp())
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


        stringBuffPayload.append(") dataMapSize:" + dataMapSize + " instructionSize:" + instructionSize);
        LOG.info(stringBuffPayload.toString());
        return hiveBot;
    }


}
