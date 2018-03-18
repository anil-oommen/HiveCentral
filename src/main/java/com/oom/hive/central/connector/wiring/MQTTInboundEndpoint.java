package com.oom.hive.central.connector.wiring;

import com.oom.hive.central.connector.MQTTInboundHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.FixedSubscriberChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
@Profile({"ModuleMQTT","Production"})
public class MQTTInboundEndpoint {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MQTTInboundEndpoint.class);

    @Value( "${mqtt.url}" )
    private String mqttUrl;

    @Value( "${mqtt.clientid.prefix}" )
    private String mqttClientPrefix;

    @Value( "${mqtt.topic.system.log:}" )
    private String mqttTopic_SYSLOG;



    /*
     *  Base Configuration Details
     */

    private String getMqttClientIdForInbound(){
        String hostName ="UNKNOWN";
        try { hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {}
        return mqttClientPrefix +"." + hostName+".receiver";
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        logger.info("MQTT Server URL " + mqttUrl);
        factory.setServerURIs(mqttUrl);
        //factory.setUserName("guest");
        //factory.setPassword("guest");
        factory.setConnectionTimeout(5000);
        factory.setKeepAliveInterval(1000);

        return factory;
    }


    /*
     * SpringIntegration Input Wiring
     */

    @Value( "${mqtt.topic.controller.receive}" )
    private String mqttControllerRecieveTopic;

    @Bean
    public MessageChannel mqttInputChannel() {
        return new FixedSubscriberChannel(mqttInputHandler());
    }

    @Autowired
    MQTTInboundHandler mqttMessageHandler;

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttInputHandler(){
        return mqttMessageHandler;
    }

    @Bean
    public MessageProducer mqttMessageProducer() {
        logger.info("MQTT Registering Client: " + getMqttClientIdForInbound() +" topic: " + mqttControllerRecieveTopic);
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                getMqttClientIdForInbound(),
                mqttClientFactory(), mqttControllerRecieveTopic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(mqttInputChannel());
        adapter.setQos(1);
        return adapter;
    }


}
