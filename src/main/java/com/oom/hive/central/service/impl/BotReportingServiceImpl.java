package com.oom.hive.central.service.impl;

import com.oom.hive.central.AppSettings;
import com.oom.hive.central.controller.BotController;
import com.oom.hive.central.mappers.BotClientDataMapper;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.repository.HiveBotEventsRepository;
import com.oom.hive.central.repository.HiveBotRepository;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.repository.model.HiveBotEvent;

import com.oom.hive.central.service.BotReportingService;
import com.oom.hive.central.utils.AirconIRMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class BotReportingServiceImpl implements BotReportingService{


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BotReportingServiceImpl.class);

    @Autowired
    HiveBotRepository hiveBotRepo;

    @Autowired
    HiveBotEventsRepository hiveBotEventRepo;



    private String getSaltedHash(String content){
        return DigestUtils.sha256Hex(
                "HiveCentral.Salt" +
                        content);
    }


    public String register(String botId, String botVersion){

        /* Persistentace to MongoDB */
        HiveBot hiveBot = hiveBotRepo.findByBotId(botId);
        if(hiveBot==null){
            hiveBot = new HiveBot();
            hiveBot.setBotId(botId);
        }
        hiveBot.setLastHearbeat(new java.util.Date());
        hiveBot.setBotVersion(botVersion);
        hiveBot.setStatus("REGISTERED");

        hiveBotRepo.save(hiveBot);

        return getSaltedHash(botId);
    }

    public boolean authenticate(String botId, String botAccesKey){
        HiveBot hiveBot = hiveBotRepo.findByBotId(botId);
        //System.out.println(getSaltedHash(botId));
        return (hiveBot!=null
                && botAccesKey.length() >= 5
                && getSaltedHash(botId).startsWith(botAccesKey));
    }

    public HiveBot getBot(String botId){
        return hiveBotRepo.findByBotId(botId);
        //return botClientRepo.findByBotId(botId);
    }

    public HiveBot markInstructionCompleted(HiveBotData botData,
                                            long instrId,
                                            String command,
                                            String result,
                                            EnumSet<AppSettings.HiveSaveOperation> saveOperations
    ){
        HiveBot hiveBot = getBot(botData.getHiveBotId());
        if(hiveBot==null)
            return null;
        hiveBot = BotClientDataMapper.enrichFromJSON(
                hiveBot,
                botData,
                saveOperations
        );

        boolean instructionRemoved = hiveBot.getInstructions()
                .removeIf(
                        instruction->
                                instrId==instruction.getInstrId() && (instruction.getSchedule()==null || !instruction.getSchedule().startsWith("schedule."))
                );

        AtomicBoolean foundInstructionToUpdateAsExecuteCompleted = new AtomicBoolean(false);
        hiveBot.getInstructions().forEach(
                instruction -> {
                    if(instruction.getInstrId()==instrId){ instruction.setExecute(false); foundInstructionToUpdateAsExecuteCompleted.set(true);}
                }

        );


        hiveBotRepo.save(hiveBot);

        logger.info("Marked Instruction. Removed(if.Onetime):" + instructionRemoved +",  Updated(if.Scheduled).Execute.Reset:" + foundInstructionToUpdateAsExecuteCompleted);

        HiveBotEvent hiveBotEvent = new HiveBotEvent();
        hiveBotEvent.setBotId(hiveBot.getBotId());
        hiveBotEvent.setTime(hiveBot.getLastHearbeat());
        hiveBotEvent.setKey("InstructionComplete:"+command);
        hiveBotEvent.setValue(
                "id:" + instrId + ", result:" + result
        );
        hiveBotEventRepo.save(hiveBotEvent);
        return hiveBot;
    }

    public void saveBot(HiveBot hiveBot){
        hiveBotRepo.save(hiveBot);
    }

    public HiveBot saveBot(HiveBotData botData,
                           EnumSet<AppSettings.HiveSaveOperation> saveOperations){
        //client.getDataSet();
        HiveBot hiveBot = getBot(botData.getHiveBotId());
        if(hiveBot==null)
            return null;
        hiveBot = BotClientDataMapper.enrichFromJSON(
                hiveBot,
                botData,
                saveOperations
        );




        //hiveBot = airconIRMapper.enrichDataSet(hiveBot);
        hiveBotRepo.save(hiveBot);


        //Save the Events Separately
        if(saveOperations.contains(AppSettings.HiveSaveOperation.EVENTLOG_DATAMAP)) {
            for (Map.Entry<String, String> entry : botData.getDataMap().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                HiveBotEvent hiveBotEvent = new HiveBotEvent();
                hiveBotEvent.setBotId(hiveBot.getBotId());
                hiveBotEvent.setTime(hiveBot.getLastHearbeat());
                hiveBotEvent.setKey(key);
                hiveBotEvent.setValue(value);
                hiveBotEventRepo.save(hiveBotEvent);
            }
        }

        return hiveBot;
    }


    @Override
    public Iterable<HiveBot> listAllHiveBots(){
        return  hiveBotRepo.findAll();
    }
}
