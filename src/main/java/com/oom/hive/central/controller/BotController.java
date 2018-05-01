package com.oom.hive.central.controller;

import com.oom.hive.central.AppSettings;
import com.oom.hive.central.exception.BotDataParseException;
import com.oom.hive.central.mappers.BotClientDataMapper;
import com.oom.hive.central.mappers.DataModeToServiceModel;
import com.oom.hive.central.model.HiveCentralResponse;
import com.oom.hive.central.model.HiveBotData;
import com.oom.hive.central.model.job.InstructionJobSchedule;
import com.oom.hive.central.quartz.InstructionScheduler;
import com.oom.hive.central.repository.model.HiveBot;
import com.oom.hive.central.service.BotNotificationService;
import com.oom.hive.central.service.BotReportingService;
import com.oom.hive.central.utils.JsonLogHelper;
import io.swagger.annotations.Api;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.EnumSet;
import java.util.List;

@CrossOrigin(
        maxAge = 3600 ,
        allowedHeaders = "*"
)
@RestController
@RequestMapping("/api/hivecentral")
@Api(value="hivecentral", description="Gateway for all Bot Communication.")
public class BotController {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BotController.class);


    @Autowired
    BotReportingService reportingService;

    @Autowired
    private BotNotificationService notificationService;



    @Autowired
    InstructionScheduler instructionScheduler;

    @Autowired
    JsonLogHelper jsonLogHelper;

    @RequestMapping(value= "/public/all.clients", method = RequestMethod.GET )
    public HiveCentralResponse allClients(){
        HiveCentralResponse rep = new HiveCentralResponse("OK", "List of Clients");
        //rep.setClients(reportingService.listAllBotClients());
        rep.setBots(reportingService.listAllHiveBots());
        return rep;
    }

    @RequestMapping(value= "/public/all.scheduled", method = RequestMethod.GET )
    public List<InstructionJobSchedule> allScheduledJobs(){
        return instructionScheduler.getScheduledJobs();
    }

    /*
    @RequestMapping(value= "/secure/remove.scheduled/{hiveBotId}/{instrjobkey}", method = RequestMethod.GET )
    public ResponseEntity<String> removeScheduledJob(
            @PathVariable(value = "hiveBotId") String hiveBotId,
            @PathVariable(value = "instrjobkey") String instructionJobKey
    ){

        if(instructionScheduler.removeScheduleByKey(hiveBotId,instructionJobKey)){
            return new ResponseEntity<>("OK", HttpStatus.ACCEPTED);
        }else{
            return new ResponseEntity<>("Data Not Found", HttpStatus.NOT_FOUND);
        }
    }*/



    @RequestMapping(value= "/secure/register.new", method = RequestMethod.POST )
    public ResponseEntity<HiveBotData> register(
            @RequestBody HiveBotData botData)
    {

        HiveBotData responseBotData = new HiveBotData();
        responseBotData.setHiveBotId(botData.getHiveBotId());
        responseBotData.setHiveBotVersion(botData.getHiveBotVersion());
        //Authenticate Bot
        if(!StringUtils.isEmpty(botData.getHiveBotId())){
            responseBotData.setAccessKey(reportingService.register(botData.getHiveBotId(),botData.getHiveBotVersion()));
            responseBotData.setMessage("Hello. '" + botData.getHiveBotId() +"'. Welcome to HiveCentral. " +
                    "Use AccessKey for further xchange.");
            responseBotData.setStatus("ACK");
            return new ResponseEntity<>(responseBotData,HttpStatus.ACCEPTED);
        }else{
            responseBotData.setStatus("ERR.BAD_REQUEST");
            return new ResponseEntity<>(responseBotData,HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value= "/public/get.info", method = RequestMethod.POST )
    public ResponseEntity<HiveBotData> getInfo(
            HttpServletRequest request,
            @RequestBody HiveBotData botData
    ){
        logger.debug("\r\nJSON::REQUEST:" + request.getRemoteAddr() +" " +
                request.getRequestURL()+"\r\n" +
                jsonLogHelper.toJSONString(botData));

        //response placeholder.
        ResponseEntity<HiveBotData> responseEntity;
        HiveBotData responseBotData = new HiveBotData();
        responseBotData.setHiveBotId(botData.getHiveBotId());
        responseBotData.setHiveBotVersion(botData.getHiveBotVersion());

        try {
            //Authenticate Bot
            if (!StringUtils.isEmpty(botData.getHiveBotId())
                    && reportingService.authenticate(botData.getHiveBotId(), botData.getAccessKey())
                    ) {
                logger.info("HTTP get_info  >  >  > " +
                        botData.getHiveBotId() +")"

                );
                HiveBot hiveBot = reportingService.getBot(botData.getHiveBotId());
                responseBotData = DataModeToServiceModel.buildBasicJsonData(hiveBot);
                responseBotData = DataModeToServiceModel.enrichFunctions(hiveBot,responseBotData);
                responseBotData = DataModeToServiceModel.enrichFullDataMap(hiveBot,responseBotData);
                responseBotData = DataModeToServiceModel.enrichFullInstructions(hiveBot,responseBotData);
                responseBotData = DataModeToServiceModel.enrichAliveInfo(hiveBot,responseBotData);

                responseBotData.setMessage("Hello " + botData.getHiveBotId() + ".Data Retrieve Done");
                responseBotData.setStatus("ACK");
                responseEntity = new ResponseEntity<>(responseBotData, HttpStatus.ACCEPTED);
            } else {
                logger.warn("HTTP Authentication Failed: " + request.getRequestURI() + " from:" + request.getRemoteHost());
                logger.warn("HTTP Creds: " + botData.getHiveBotId() +" " +  botData.getAccessKey());
                responseBotData.setStatus("ERR.UNAUTHORISED");
                responseEntity =new ResponseEntity<>(responseBotData, HttpStatus.UNAUTHORIZED);
            }
        }catch(BotDataParseException bEx){
            logger.warn("HTTP BadRequest Rejected:" + request.getRequestURI() + " from:" + request.getRemoteHost());
            responseBotData.setStatus("ERR.BAD_REQUEST");
            responseBotData.setMessage(bEx.getMessage());
            responseEntity =  new ResponseEntity<>(responseBotData, HttpStatus.BAD_REQUEST);
        }

        logger.debug("\r\nJSON::RESPONSE\r\n" + jsonLogHelper.toJSONString(responseEntity.getBody()));
        return responseEntity;
    }


    @RequestMapping(value= "/secure/save.{options}", method = {RequestMethod.OPTIONS,RequestMethod.POST} )
    public ResponseEntity<HiveBotData> saveInfo(
            @PathVariable("options") String saveoptions,
            @RequestBody HiveBotData botData,
            HttpServletRequest request
    )
    {
        logger.debug("\r\nJSON::REQUEST:" + request.getRemoteAddr() +" " +
                request.getRequestURL()+"\r\n" +
                jsonLogHelper.toJSONString(botData));

        //response placeholder.
        ResponseEntity<HiveBotData> responseEntity;
        HiveBotData responseBotData = new HiveBotData();
        responseBotData.setHiveBotId(botData.getHiveBotId());
        responseBotData.setHiveBotVersion(botData.getHiveBotVersion());

        try {
            //Authenticate Bot
            if (!StringUtils.isEmpty(botData.getHiveBotId())
                    && reportingService.authenticate(botData.getHiveBotId(), botData.getAccessKey())
                    ) {
                logger.info("HTTP save.info  >  >  > " +
                        botData.getHiveBotId() +":operation=(" + AppSettings.hiveOperationsToString(_translateSaveOperations(saveoptions)) +")"

                );

                HiveBot hiveBot = reportingService.saveBot(
                        botData,_translateSaveOperations(saveoptions)
                );

                boolean hasSchedule = instructionScheduler
                        .registerNewInboundInstruction(botData,
                                _translateSaveOperations(saveoptions));

                responseBotData = DataModeToServiceModel.buildBasicJsonData(hiveBot);
                responseBotData = DataModeToServiceModel.enrichFunctions(hiveBot,responseBotData);
                responseBotData = DataModeToServiceModel.enrichFullDataMap(hiveBot,responseBotData);
                responseBotData = DataModeToServiceModel.enrichFullInstructions(hiveBot,responseBotData);
                responseBotData = DataModeToServiceModel.enrichAliveInfo(hiveBot,responseBotData);


                responseBotData.setMessage("Hello " +
                        botData.getHiveBotId() + ". Data Info Saved. " +
                        (hasSchedule?
                                " Instructions Scheduled.":"")
                );
                notificationService.updateFunctions(hiveBot);
                responseBotData.setStatus("ACK");
                responseEntity = new ResponseEntity<>(responseBotData, HttpStatus.ACCEPTED);


            } else {
                logger.warn("HTTP Authentication Failed: " + request.getRequestURI() + " from:" + request.getRemoteHost());
                logger.warn("HTTP Creds: " + botData.getHiveBotId() +" " +  botData.getAccessKey());
                responseBotData.setStatus("ERR.UNAUTHORISED");
                responseEntity =new ResponseEntity<>(responseBotData, HttpStatus.UNAUTHORIZED);
            }
        }catch(BotDataParseException bEx){
            logger.warn("HTTP BadRequest Rejected:" + request.getRequestURI() + " from:" + request.getRemoteHost());
            responseBotData.setStatus("ERR.BAD_REQUEST");
            responseBotData.setMessage(bEx.getMessage());
            responseEntity =  new ResponseEntity<>(responseBotData, HttpStatus.BAD_REQUEST);
        }

        logger.debug("\r\nJSON::RESPONSE\r\n" + jsonLogHelper.toJSONString(responseEntity.getBody()));
        return responseEntity;
    }




    /*
    @RequestMapping(value= "/{bottype}/xchange/{action}", method = RequestMethod.POST )
    @Deprecated
    public ResponseEntity<HiveBotData> xchange(
            @PathVariable("bottype") String bottype,
            @PathVariable("action") String action,
            /* for action='EXECUTED'
            @RequestParam(value = "exe.instruction.command", required = false) String instruction_command,
            @RequestParam(value = "exe.instruction.id", required = false) String instruction_id1,
            @RequestParam(value = "exe.instruction.result", required=false) String instruction_result, * /
            @RequestBody HiveBotData botData,
            HttpServletRequest request
            )
    {
        logger.debug("\r\nJSON::REQUEST:" + request.getRemoteAddr() +" " +
                request.getRequestURL()+"\r\n" +
                jsonLogHelper.toJSONString(botData));

        //response placeholder.
        ResponseEntity<HiveBotData> responseEntity;
        HiveBotData responseBotData = new HiveBotData();
        responseBotData.setHiveBotId(botData.getHiveBotId());
        responseBotData.setHiveBotVersion(botData.getHiveBotVersion());

        try {
            //Authenticate Bot
            if (!StringUtils.isEmpty(botData.getHiveBotId())
                    && reportingService.authenticate(botData.getHiveBotId(), botData.getAccessKey())
                    ) {
                logger.info("HTTP xchange  >  >  > " +
                        botData.getHiveBotId() +":operation=(" + AppSettings.hiveOperationsToString(_translateSaveOperations(action)) +")"

                );

                if ("get_info".equalsIgnoreCase(action)) {
                    HiveBot hiveBot = reportingService.getBot(botData.getHiveBotId());
                    responseBotData = DataModeToServiceModel.buildBasicJsonData(hiveBot);
                    responseBotData = DataModeToServiceModel.enrichFunctions(hiveBot,responseBotData);
                    responseBotData = DataModeToServiceModel.enrichFullDataMap(hiveBot,responseBotData);
                    responseBotData = DataModeToServiceModel.enrichFullInstructions(hiveBot,responseBotData);
                    responseBotData = DataModeToServiceModel.enrichAliveInfo(hiveBot,responseBotData);

                    responseBotData.setMessage("Hello " + botData.getHiveBotId() + ".Data Retrieve Done");
                    responseBotData.setStatus("ACK");
                    responseEntity = new ResponseEntity<>(responseBotData, HttpStatus.ACCEPTED);
                }else if (action.toLowerCase().startsWith("save_")){
                    HiveBot hiveBot = reportingService.saveBot(
                            botData,_translateSaveOperations(action)
                    );

                    boolean hasSchedule = instructionScheduler
                            .registerNewInboundInstruction(botData,
                                    _translateSaveOperations(action));

                    responseBotData = DataModeToServiceModel.buildBasicJsonData(hiveBot);
                    responseBotData = DataModeToServiceModel.enrichFunctions(hiveBot,responseBotData);
                    responseBotData = DataModeToServiceModel.enrichFullDataMap(hiveBot,responseBotData);
                    responseBotData = DataModeToServiceModel.enrichFullInstructions(hiveBot,responseBotData);
                    responseBotData = DataModeToServiceModel.enrichAliveInfo(hiveBot,responseBotData);


                    responseBotData.setMessage("Hello " +
                            botData.getHiveBotId() + ". Data Info Saved. " +
                            (hasSchedule?
                                    " Instructions Scheduled.":"")
                    );
                    notificationService.updateFunctions(hiveBot);
                    responseBotData.setStatus("ACK");
                    responseEntity = new ResponseEntity<>(responseBotData, HttpStatus.ACCEPTED);
                }else{
                    throw new BotDataParseException("Unsupported Action:" + action);
                }


            } else {
                logger.warn("HTTP Authentication Failed: " + request.getRequestURI() + " from:" + request.getRemoteHost());
                logger.warn("HTTP Creds: " + botData.getHiveBotId() +" " +  botData.getAccessKey());
                responseBotData.setStatus("ERR.UNAUTHORISED");
                responseEntity =new ResponseEntity<>(responseBotData, HttpStatus.UNAUTHORIZED);
            }
        }catch(BotDataParseException bEx){
            logger.warn("HTTP BadRequest Rejected:" + request.getRequestURI() + " from:" + request.getRemoteHost());
            responseBotData.setStatus("ERR.BAD_REQUEST");
            responseBotData.setMessage(bEx.getMessage());
            responseEntity =  new ResponseEntity<>(responseBotData, HttpStatus.BAD_REQUEST);
        }

        logger.debug("\r\nJSON::RESPONSE\r\n" + jsonLogHelper.toJSONString(responseEntity.getBody()));
        return responseEntity;
    }
    */

    private EnumSet<AppSettings.HiveSaveOperation> _translateSaveOperations(String action){
        EnumSet<AppSettings.HiveSaveOperation> saveOperations =
                EnumSet.of(AppSettings.HiveSaveOperation.SAVE_INFO);
        if(action.toLowerCase().contains("add_instructions"))
            saveOperations.add(AppSettings.HiveSaveOperation.ADD_INSTRUCTIONS);
        if(action.toLowerCase().contains("set_instructions"))
            saveOperations.add(AppSettings.HiveSaveOperation.SET_INSTRUCTIONS);
        if(action.toLowerCase().contains("set_datamap"))
            saveOperations.add(AppSettings.HiveSaveOperation.SET_DATAMAP);
        if(action.toLowerCase().contains("add_datamap"))
            saveOperations.add(AppSettings.HiveSaveOperation.ADD_DATAMAP);
        if(action.toLowerCase().contains("log_datamap"))
            saveOperations.add(AppSettings.HiveSaveOperation.EVENTLOG_DATAMAP);
        return saveOperations;
    }


}
