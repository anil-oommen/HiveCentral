package com.oom.hive.central.controller;

import com.oom.hive.central.AppSettings;
import com.oom.hive.central.exception.BotDataParseException;
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

    static final String RESPONSE_STATUS_BAD_REQUEST= "ERR.BAD_REQUEST";
    static final String RESPONSE_STATUS_ACK= "ACK";
    static final String RESPONSE_STATUS_ERR_UNAUTHORISED = "ERR.UNAUTHORISED";

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
        rep.setBots(reportingService.listAllHiveBots());
        return rep;
    }

    @RequestMapping(value= "/public/all.scheduled", method = RequestMethod.GET )
    public List<InstructionJobSchedule> allScheduledJobs(){
        return instructionScheduler.getScheduledJobs();
    }


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
            responseBotData.setStatus(RESPONSE_STATUS_ACK);
            return new ResponseEntity<>(responseBotData,HttpStatus.ACCEPTED);
        }else{
            responseBotData.setStatus(RESPONSE_STATUS_BAD_REQUEST);
            return new ResponseEntity<>(responseBotData,HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value= "/public/get.info", method = RequestMethod.POST )
    public ResponseEntity<HiveBotData> getInfo(
            HttpServletRequest request,
            @RequestBody HiveBotData botData
    ){
        if(logger.isDebugEnabled()) {
            logger.debug("\r\nJSON::REQUEST: {} {} \r\n {}",
                    request.getRemoteAddr(),
                    request.getRequestURL(),
                    jsonLogHelper.toJSONString(botData)
            );
        }

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
                responseBotData.setStatus(RESPONSE_STATUS_ACK);
                responseEntity = new ResponseEntity<>(responseBotData, HttpStatus.ACCEPTED);
            } else {
                logger.warn("HTTP Authentication Failed: {} from: {}" ,request.getRequestURI(), request.getRemoteHost());
                logger.warn("HTTP Creds: {} {}"  ,  botData.getHiveBotId(), botData.getAccessKey());
                responseBotData.setStatus(RESPONSE_STATUS_ERR_UNAUTHORISED);
                responseEntity =new ResponseEntity<>(responseBotData, HttpStatus.UNAUTHORIZED);
            }
        }catch(BotDataParseException bEx){
            logger.warn("HTTP BadRequest Rejected:{} from: {}"   , request.getRequestURI(), request.getRemoteHost());
            responseBotData.setStatus(RESPONSE_STATUS_BAD_REQUEST);
            responseBotData.setMessage(bEx.getMessage());
            responseEntity =  new ResponseEntity<>(responseBotData, HttpStatus.BAD_REQUEST);
        }

        if(logger.isDebugEnabled())
            logger.debug("\r\nJSON::RESPONSE\r\n{}" , jsonLogHelper.toJSONString(responseEntity.getBody()));
        return responseEntity;
    }


    @RequestMapping(value= "/secure/save.{options}", method = {RequestMethod.OPTIONS,RequestMethod.POST} )
    public ResponseEntity<HiveBotData> saveInfo(
            @PathVariable("options") String saveoptions,
            @RequestBody HiveBotData botData,
            HttpServletRequest request
    )
    {
        if(logger.isDebugEnabled()) {
            logger.debug("\r\nJSON::REQUEST:{} {} \r\n {} " ,
                    request.getRemoteAddr(),
                    request.getRequestURL() ,
                    jsonLogHelper.toJSONString(botData));
        }
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
                logger.info("HTTP save.info  >  >  > {} :operation=({})" ,
                        botData.getHiveBotId() , AppSettings.hiveOperationsToString(p_translateSaveOperations(saveoptions))

                );

                HiveBot hiveBot = reportingService.saveBot(
                        botData,p_translateSaveOperations(saveoptions)
                );

                boolean hasSchedule = instructionScheduler
                        .registerNewInboundInstruction(botData,
                                p_translateSaveOperations(saveoptions));

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
                responseBotData.setStatus(RESPONSE_STATUS_ACK);
                responseEntity = new ResponseEntity<>(responseBotData, HttpStatus.ACCEPTED);


            } else {
                logger.warn("HTTP Authentication Failed: {} from: {}" , request.getRequestURI(), request.getRemoteHost());
                logger.warn("HTTP Creds: {} {} "   , botData.getHiveBotId(), botData.getAccessKey());
                responseBotData.setStatus(RESPONSE_STATUS_ERR_UNAUTHORISED);
                responseEntity =new ResponseEntity<>(responseBotData, HttpStatus.UNAUTHORIZED);
            }
        }catch(BotDataParseException bEx){
            logger.warn("HTTP BadRequest Rejected:{} from: {}",
                    request.getRequestURI() ,
                    request.getRemoteHost());
            responseBotData.setStatus(RESPONSE_STATUS_BAD_REQUEST);
            responseBotData.setMessage(bEx.getMessage());
            responseEntity =  new ResponseEntity<>(responseBotData, HttpStatus.BAD_REQUEST);
        }

        logger.debug("\r\nJSON::RESPONSE\r\n {}" , jsonLogHelper.toJSONString(responseEntity.getBody()));
        return responseEntity;
    }

    private EnumSet<AppSettings.HiveSaveOperation> p_translateSaveOperations(String action){
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
