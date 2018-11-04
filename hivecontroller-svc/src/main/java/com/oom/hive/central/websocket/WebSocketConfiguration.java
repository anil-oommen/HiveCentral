package com.oom.hive.central.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration extends AbstractWebSocketMessageBrokerConfigurer{

    /*  Enabling SockJS fallback options so that alternate transports may be used if WebSocket
    *   is not available. The SockJS client will attempt to connect to "/{value provided}"
    *   and use the best transport available (websocket, xhr-streaming, xhr-polling, etc).
    */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket-discovery")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    final static public String WEBSOCK_DESTINATION_TOPIC_BASE = "/websocket/topic";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/hivewebsocket")    // The Service EndPoint
                .enableSimpleBroker(WEBSOCK_DESTINATION_TOPIC_BASE);   //Messaging Base Topic
    }
}