package com.oom.hive.central.connector.wiring;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oom.hive.central.model.HiveBotData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.json.ObjectToJsonTransformer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.messaging.*;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;


@Component
@Profile({"ModuleMQTT"})
public class MQTTOutboundEndpoint {


    /*
     *  Reference Implementation
     *  https://docs.spring.io/spring-integration/reference/html/mqtt.html
     *  https://dzone.com/articles/message-processing-spring
     *  http://www.baeldung.com/spring-integration
     *  https://docs.spring.io/spring-integration/reference/html/messaging-transformation-chapter.html
     */

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MQTTOutboundEndpoint.class);

    @Value( "${mqtt.url}" )
    private String mqttUrl;

    @Value( "${mqtt.user}" )
    private String mqttUser;

    @Value( "${mqtt.password}" )
    private String mqttPassword;


    @Value( "${mqtt.clientid.prefix}" )
    private String mqttClientPrefix;


    /*
     *  Base Configuration Details
     */

    private String getMqttClientIdForOutbound(){
        String hostName ="UNKNOWN";
        try { hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.error("Error getting Host for MQTT Outbound",e);
        }
        return mqttClientPrefix +"." + hostName+".sender";
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        logger.info("MQTT Server URL {}", mqttUrl);
        factory.setServerURIs(mqttUrl);
        if(StringUtils.isEmpty(mqttUser)){
            logger.warn("MQTT No UserName/Password set. Will connect without any");
        }else{
            logger.warn("MQTT Connecting with UserName: {} Passsword:XXXXX",mqttUser);
            factory.setUserName(mqttUser);
            factory.setPassword(mqttPassword);
        }
        factory.setConnectionTimeout(5000);
        factory.setKeepAliveInterval(1000);

        return factory;
    }


    /*
     * SpringIntegration Input Wiring
     */

    /*
     * SpringIntegration Output Wiring
     */

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Router(inputChannel="mqttOutboundChannel")
    public String route(HiveBotData botData) {
        String destinationChannel = "pubDiscardJSONTransformer"; // Default
        if(botData.getHiveBotId().contains(mqttMicroClimateBotPattern)){
            destinationChannel =  "pubMicroClimateJSONTransform";
        }else if(botData.getHiveBotId().contains(mqttWeaFcastCollectorBotPattern)){
            destinationChannel = "pubWeaFcastCollectorJSONTransform";
        }
        logger.info("MQTT Routing  > {} to '{}'", botData.getHiveBotId(),destinationChannel );
        return destinationChannel;
    }

    public static Jackson2JsonObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return new Jackson2JsonObjectMapper(mapper);
    }


    /* ******************************************************
    * 'microclima' ServiceActivator & Transformer
    *
    * *******************************************************/
    @Value( "${mqtt.topic.publish.botclients.microclimate}" )
    private String mqttMicroClimatePublishTopic;
    @Value ("MICLIM")
    private String mqttMicroClimateBotPattern;

    //@Value( "${mqtt.topic.microclima.publish.retained.will}" )
    //private String mqttMicroClimateRetainedWillTopic;

    @Bean
    @Transformer(
            inputChannel = "pubMicroClimateJSONTransform",
            outputChannel = "pubMicroClimateQueue")
    public ObjectToJsonTransformer transformMicroClimateMessage() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        logger.trace("Transformer Object >>> JSON" );
        return new ObjectToJsonTransformer(new Jackson2JsonObjectMapper(mapper));
    }

    @Bean
    @ServiceActivator(inputChannel = "pubMicroClimateQueue")
    public MessageHandler mqttOutboundMicroClimate() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(
                        getMqttClientIdForOutbound(),
                        mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(mqttMicroClimatePublishTopic);
        return messageHandler;
    }


    /* ******************************************************
     * 'neacollector' ServiceActivator & Transformer
     *
     * *******************************************************/
    @Value( "${mqtt.topic.publish.controller.weatforecast}" )
    private String mqttWeaFcastCollectorPublishTopic;
    @Value ("GOVSG.WEATHER")
    private String mqttWeaFcastCollectorBotPattern;


    @Bean
    @Transformer(
            inputChannel = "pubWeaFcastCollectorJSONTransform",
            outputChannel = "pubWeaFcastCollectorQueue")
    public ObjectToJsonTransformer transformWeaFcastCollectorMessage() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        logger.trace("Transformer Object >>> JSON" );
        return new ObjectToJsonTransformer(new Jackson2JsonObjectMapper(mapper));
    }

    @Bean
    @ServiceActivator(inputChannel = "pubWeaFcastCollectorQueue")
    public MessageHandler mqttOutboundWeaFcastCollector() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(
                        getMqttClientIdForOutbound(),
                        mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(mqttWeaFcastCollectorPublishTopic);
        return messageHandler;
    }

    /* ******************************************************
     * 'Discarded' ServiceActivator & Transformer
     *
     * *******************************************************/

    @Bean
    @Transformer(
            inputChannel = "pubDiscardJSONTransformer",
            outputChannel = "pubDiscardDeadQueue")
    public org.springframework.integration.transformer.Transformer transformDiscardMessages() {
        return new ObjectToJsonTransformer();
    }

    @ServiceActivator(inputChannel = "pubDiscardDeadQueue")
    public void handleDiscardDeadQueue(String botData) {
        logger.error("Discarded Message intended for Publish {}" , botData);
    }


}
