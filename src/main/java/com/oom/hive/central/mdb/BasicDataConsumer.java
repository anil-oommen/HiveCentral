package com.oom.hive.central.mdb;


import com.oom.hive.central.mdb.base.BotBaseDataConsumer;
import com.oom.hive.central.mdb.base.ConsumerSubQueue;
import com.oom.hive.central.repository.model.HiveBot;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@ConsumerSubQueue({"weatforecast","add more here"})
@Component
@Profile({"ModuleMQTT"})
public class BasicDataConsumer extends BotBaseDataConsumer {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BasicDataConsumer.class);

    @Override
    public void postSaveSensorData(HiveBot botData){
        //No Specific Action.
    }


    @Override
    public void postBotBootup(HiveBot botData){
        //No Specific Action.
    }

    @Override
    public void postBotInstructionCompleted(HiveBot botData){
        //No Specific Action.
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
