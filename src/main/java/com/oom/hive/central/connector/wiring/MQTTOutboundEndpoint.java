package com.oom.hive.central.connector.wiring;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oom.hive.central.model.HiveBotData;
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
@Profile({"ModuleMQTT","Production"})
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

    @Value( "${mqtt.clientid.prefix}" )
    private String mqttClientPrefix;


    /*
     *  Base Configuration Details
     */

    private String getMqttClientIdForOutbound(){
        String hostName ="UNKNOWN";
        try { hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {}
        return mqttClientPrefix +"." + hostName+".sender";
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
    /*
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
        String destinationChannel = "mqttOutboundJSONTransformDiscardDeadQueue"; // Default
        if(botData.getHiveBotId().contains("MICLIM")){
            destinationChannel =  "mqttOutboundJSONTransformMicroClimateBOT";
        }
        logger.info("MQTT Routing  > "+ botData.getHiveBotId() + " to '" + destinationChannel + "'");
        return destinationChannel;
    }

    public static Jackson2JsonObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return new Jackson2JsonObjectMapper(mapper);
    }


    /* 'microclima' ServiceActivator */
    @Value( "${mqtt.topic.microclima.publish}" )
    private String mqttMicroClimatePublishTopic;

    @Value( "${mqtt.topic.microclima.publish.retained.will}" )
    private String mqttMicroClimateRetainedWillTopic;

    @Bean
    @Transformer(inputChannel = "mqttOutboundJSONTransformMicroClimateBOT",
            outputChannel = "mqttOutboundChannelMicroClimateBOT")
    public ObjectToJsonTransformer transformMicroClimateMessage() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        logger.info("Transformer Object >>> JSON" );
        return new ObjectToJsonTransformer(new Jackson2JsonObjectMapper(mapper));
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannelMicroClimateBOT")
    public MessageHandler mqttOutboundMicroClimate() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(
                        getMqttClientIdForOutbound(),
                        mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(mqttMicroClimatePublishTopic);
        return messageHandler;
    }


    /* 'Discarded' ServiceActivator */
    @Bean
    @Transformer(inputChannel = "mqttOutboundJSONTransformDiscardDeadQueue",
            outputChannel = "mqttOutboundDiscardDeadQueue")
    public org.springframework.integration.transformer.Transformer transformDiscardMessages() {
        logger.info("Transformer Object >>> JSON" );
        return new ObjectToJsonTransformer();
    }

    @ServiceActivator(inputChannel = "mqttOutboundDiscardDeadQueue")
    public void handleDiscardDeadQueue(String botData) {
        logger.error("Discarded Message " + botData);
    }


}
