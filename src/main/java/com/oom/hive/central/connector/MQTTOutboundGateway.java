package com.oom.hive.central.connector;

import com.oom.hive.central.model.HiveBotData;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MQTTOutboundGateway {
    void sendToMqtt(HiveBotData botData);
}
