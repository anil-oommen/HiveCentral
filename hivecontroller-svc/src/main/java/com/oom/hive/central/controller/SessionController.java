package com.oom.hive.central.controller;

import com.oom.hive.central.model.GenericMessage;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.HiveCentralResponse;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@CrossOrigin(
        maxAge = 3600 ,
        allowedHeaders = "*"
)
@RestController
@RequestMapping("/api/session")
@Api(value="session", description="User Session Controller")
public class SessionController {


    @RequestMapping(value= "/secure/check.access", method = {RequestMethod.GET, RequestMethod.POST} )
    public ResponseEntity<GenericMessage> secureAccessCheck(){
        SecurityContext context = SecurityContextHolder.getContext();
        return new ResponseEntity<>(
                new GenericMessage(0,"User:" + context.getAuthentication().getName()),
                HttpStatus.ACCEPTED
        );
    }

    @RequestMapping(value= "/public/check.access", method = {RequestMethod.GET , RequestMethod.POST})
    public ResponseEntity<GenericMessage> publicAccessCheck(){
        return new ResponseEntity<>(
                new GenericMessage(0,"Ack:" ),
                HttpStatus.ACCEPTED
        );
    }


}
