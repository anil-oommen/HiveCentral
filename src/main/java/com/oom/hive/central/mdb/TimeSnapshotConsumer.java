package com.oom.hive.central.mdb;


import com.oom.hive.central.mdb.base.BaseConsumer;
import com.oom.hive.central.mdb.base.ConsumerSubQueue;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@ConsumerSubQueue("time-snapshot")
@Component
@Profile({"ModuleMQTT"})
public class TimeSnapshotConsumer implements BaseConsumer {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TimeSnapshotConsumer.class);

    @Override
    public void handleMessage(String messageData) {
        logger.error("########### TODO Handler To be implemnter");
    }


}
