package com.oom.hive.central.controller;

import com.oom.hive.central.websocket.WebSocketConfiguration;
import io.swagger.annotations.Api;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Api(value="hivewebsocket", description="Websocket Controller")
public class HVWebsocketController {

    @MessageMapping("/notification")  // So endpoint is /hivewebsocket/notification
    @SendTo(WebSocketConfiguration.WEBSOCK_DESTINATION_TOPIC_BASE + "/notify")
    public String notification(String message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return "Right back at you "  + message ;
    }
}
