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
@Profile({"ModuleMQTT"})
public class BotNotificationServiceImpl implements BotNotificationService{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BotNotificationServiceImpl.class);

    @Autowired
    private MQTTOutboundGateway mqttOutboundGateway;

    @Autowired
    private JsonLogHelper jsobLogHelper;

    private void p_sendMessage(HiveBotData hiveBotData){
        jsobLogHelper.toJSONString(hiveBotData);

        if(logger.isInfoEnabled()) {
            logger.info("MQTT Publishe  >  >  > ");
            logger.info("MQTT > Payload ({})", jsobLogHelper.toJSONString(hiveBotData));
        }

        mqttOutboundGateway.sendToMqtt(hiveBotData);
    }

    public void executeInstruction(HiveBot hiveBot, HiveBotInstruction hiveBotInstruction){
        HiveBotData hiveBotData = DataModeToServiceModel.buildBasicJsonData(hiveBot);
        hiveBotData = DataModeToServiceModel.enrichFunctions(hiveBot,hiveBotData);
        hiveBotData.setDataType(HiveBotDataType.EXECUTE_INSTRUCTION);
        p_sendSingleInstruction(hiveBot,hiveBotData,hiveBotInstruction);
    }

    public void updateFunctions(HiveBot hiveBot){
        HiveBotData hiveBotData = DataModeToServiceModel.buildBasicJsonData(hiveBot);
        hiveBotData = DataModeToServiceModel.enrichFunctions(hiveBot,hiveBotData);
        hiveBotData.setDataType(HiveBotDataType.UPDATE_FUNCTIONS);
        p_sendMessage(hiveBotData);
    }

    private void p_sendSingleInstruction(HiveBot hiveBot , HiveBotData hiveBotData, HiveBotInstruction hiveBotInstruction){
        hiveBotData.getInstructions()
                .add(new Instruction(
                        hiveBotInstruction.getInstrId(),
                        hiveBotInstruction.getCommand(),
                        hiveBotInstruction.getSchedule(),
                        hiveBotInstruction.getParams(),
                        hiveBotInstruction.isExecute()
                ));
        p_sendMessage(hiveBotData);
    }


    private void p_sendAllInstructionsIndividually(HiveBot hiveBot, HiveBotData hiveBotData){
        hiveBot.getInstructions()
                .forEach(instr->{
                    if(instr.isExecute()){
                        hiveBotData.getInstructions().clear();
                        hiveBotData.getInstructions()
                                .add(new Instruction(
                                        instr.getInstrId(),
                                        instr.getCommand(),
                                        instr.getSchedule(),
                                        instr.getParams(), instr.isExecute()
                                ));
                        p_sendMessage(hiveBotData);
                    }
                }
        );
    }

    public void sendCatchupForBotClient(HiveBot hiveBot){
        HiveBotData hiveBotData = DataModeToServiceModel.buildBasicJsonData(hiveBot);
        hiveBotData = DataModeToServiceModel.enrichFunctions(hiveBot,hiveBotData);
        hiveBotData.setDataType(HiveBotDataType.CATCHUP_POST_BOOTUP);
        //Send Basic Information without any Backlog Instruction
        p_sendMessage(hiveBotData);
        //Send all the backlog instructions , ordered
        p_sendAllInstructionsIndividually(hiveBot,hiveBotData);

    }






}
