package com.oom.hive.central.connector;

import com.oom.hive.central.exception.BotDataParseException;
import com.oom.hive.central.mdb.base.ConsumerSubQueue;
import com.oom.hive.central.mdb.base.BaseConsumer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class MQTTInboundHandler implements MessageHandler {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(MQTTInboundHandler.class);

    //@Value( "${mqtt.topic.receive.controller.base}" )
    //private String mqttControllerTopicBase;

    //private Map<String, BaseConsumer> consumers;

    @Autowired
    private org.springframework.context.ApplicationContext context;

    /*@PostConstruct
    public void initConsumers(){
        if (consumers == null) {
            consumers = new HashMap<>();
            Map<String, Object> beans = context.getBeansWithAnnotation(ConsumerSubQueue.class);
            for (Object bean : beans.values()) {
                ConsumerSubQueue bmh = bean.getClass().getAnnotation(ConsumerSubQueue.class);
                String topicQueueName = mqttControllerTopicBase + bmh.value();
                if(StringUtils.isEmpty(topicQueueName)){
                    logger.error(String.format("MQTT Topic/Queue for '%s' is not set!! Message Handlers %s not Registered"
                            ,bmh.value()
                            ,bean.getClass().getName()
                    ));
                }else{
                    logger.info(String.format("MQTT Topic/Queue '%s' Registered to Message Handlers %s."
                            ,topicQueueName
                            ,bean.getClass().getSimpleName()
                    ));
                    consumers.put(topicQueueName, (BaseConsumer) bean);

                }
            }
        }
    }*/

    public Set<BaseConsumer> findMatchingConsumer(String topicName){
        Map<String, Object> beans = context.getBeansWithAnnotation(ConsumerSubQueue.class);
        Set matchingConsumers = beans.values().stream()
                .filter( bean ->
                        Arrays.asList(bean.getClass().getAnnotation(ConsumerSubQueue.class).value())
                                .stream()
                                .filter(anval ->
                                    topicName.contains(anval)
                                )
                                .count()>0
                ).collect(Collectors.toSet());
        ;

        return matchingConsumers;
    }


    @Override
    public void handleMessage(Message<?> message) {
        try {


            String mqttTopic = message.getHeaders().get("mqtt_topic").toString();
            StringBuilder sBuffer = new StringBuilder();
            message.getHeaders().forEach((key, value) ->
                sBuffer.append(" " + key + " = " + value)
            );


            //BaseConsumer consumer = consumers.get(mqttTopic);
            logger.info("MQTT Received  <  <  on  \"{}\" finding consumer..  \"{{}}\"",
                    message.getHeaders().get("mqtt_topic"),
                    sBuffer);

            Set<BaseConsumer> consumers = findMatchingConsumer(message.getHeaders().get("mqtt_topic").toString());
            if(consumers.isEmpty()){
                logger.error("\t\t[NO_REGISTERED_CONSUMER], Discarded Message. ");
            }else{
                consumers.forEach(consumer -> {
                    logger.info("\t\tHandover to Consumer [{}]  ",consumer.getClass().getSimpleName() );
                    consumer.handleMessage(message.getPayload().toString());
                });
            }

            /*String consumerClassName = consumer!=null?consumer.getClass().getSimpleName():"<NotAvailable>";



            logger.info("MQTT Received  <  <  on  \"{}\" to [{}] \"{{}}\"",
                    message.getHeaders().get("mqtt_topic"),
                    consumerClassName,
                    sBuffer);
            logger.info("     < Payload ({}) >", message.getPayload().toString());
            if(consumer!=null) {
                consumer.handleMessage(message.getPayload().toString());
            }else{
                logger.warn("     < No Registered Consumer. Message Discarded>");
            }*/



        } catch (BotDataParseException bEx) {
            logger.warn("BadRequest Payload Ignoring: {} ", message.getPayload());
        }catch(Exception rEx){
            logger.error("Error Handling Message", rEx);
            throw rEx;
        }
    }


}
