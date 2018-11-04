package com.oom.hive.central.controller;

import com.oom.hive.central.model.GenericMessage;
import com.oom.hive.central.model.job.InstructionJobSchedule;
import com.oom.hive.central.quartz.InstructionScheduler;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(
        maxAge = 3600 ,
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST}
)
@RestController
@RequestMapping("/api/instrschedule")
@Api(value="instrschedule", description="Bot Instruction Schedule Controller")
public class InstrScheduleController {

    @Autowired
    InstructionScheduler instructionScheduler;

    @RequestMapping(value= "/public/list.all", method = {RequestMethod.GET, RequestMethod.POST })
    public List<InstructionJobSchedule> listAllScheduledJobs(){
        return instructionScheduler.getScheduledJobs();
    }

    @RequestMapping(value= "/secure/remove", method = {RequestMethod.GET ,RequestMethod.POST})
    public ResponseEntity<GenericMessage> removeScheduledJob(
            @RequestParam(value="hiveBotId"  ) String hiveBotId,
            @RequestParam(value="instrjobkey" ) String instructionJobKey
    ){

        if(instructionScheduler.removeScheduleByKey(hiveBotId,instructionJobKey)){
            return new ResponseEntity<>(new GenericMessage(0,"Removed"), HttpStatus.ACCEPTED);
        }else{
            return new ResponseEntity<>(new GenericMessage(-1,"Not Found"), HttpStatus.NOT_FOUND);
        }
    }
}
