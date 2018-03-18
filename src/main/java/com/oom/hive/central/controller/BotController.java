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
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST}
)
@RestController
@RequestMapping("/hivecentral")
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


    //@Value("#{'${hivecentral.registered.bots}'.split(',')}")
   // private List<String> registeredBots;


    /*@RequestMapping(value= "iot.aqua.bot/heartbeat", method = RequestMethod.GET)
    @ApiOperation(value = "BOT Is Alive Heartbeat",response = HiveCentralResponse.class)
    public HiveCentralResponse heartbeat(
            @RequestParam(value = "bot-id", required=false, defaultValue="NOVALUE" ) String botid,
            @RequestParam(value = "bot-version", required=false, defaultValue="" ) String botversion,
            @RequestParam(value = "bot-status", required=false, defaultValue="" ) String botstatus
    ) {
        System.out.println(botid + " " + botversion + " " + botstatus);


        if(registeredBots.contains(botid)){
            HiveCentralResponse rep = new HiveCentralResponse("OK", "Authorized and Accepted.");
            reportingService.heartbeat(botid,botversion,botstatus);
            return rep;
        }else{
            HiveCentralResponse rep = new HiveCentralResponse("ERR", "BOT not Registered:" + botid);
            return rep;
        }

    }*/

    @RequestMapping(value= "/all.clients", method = RequestMethod.GET )
    public HiveCentralResponse allClients(){
        HiveCentralResponse rep = new HiveCentralResponse("OK", "List of Clients");
        //rep.setClients(reportingService.listAllBotClients());
        rep.setBots(reportingService.listAllHiveBots());
        return rep;
    }

    @RequestMapping(value= "/all.scheduled", method = RequestMethod.GET )
    public List<InstructionJobSchedule> allScheduledJobs(){
        return instructionScheduler.getScheduledJobs();
    }

    @RequestMapping(value= "/remove.scheduled/{hiveBotId}/{instrjobkey}", method = RequestMethod.GET )
    public ResponseEntity<String> removeScheduledJob(
            @PathVariable(value = "hiveBotId") String hiveBotId,
            @PathVariable(value = "instrjobkey") String instructionJobKey
            ){

        if(instructionScheduler.removeScheduleByKey(hiveBotId,instructionJobKey)){
            return new ResponseEntity<>("OK", HttpStatus.ACCEPTED);
        }else{
            return new ResponseEntity<>("Data Not Found", HttpStatus.NOT_FOUND);
        }
    }



    @RequestMapping(value= "/{bottype}/register", method = RequestMethod.POST )
    public ResponseEntity<HiveBotData> register(
            @PathVariable("bottype") String bottype,
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



    @RequestMapping(value= "/{bottype}/xchange/{action}", method = RequestMethod.POST )
    public ResponseEntity<HiveBotData> xchange(
            @PathVariable("bottype") String bottype,
            @PathVariable("action") String action,
            /* for action='EXECUTED'
            @RequestParam(value = "exe.instruction.command", required = false) String instruction_command,
            @RequestParam(value = "exe.instruction.id", required = false) String instruction_id1,
            @RequestParam(value = "exe.instruction.result", required=false) String instruction_result, */
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

                /* Deprecated , now using MQTT
                } else if (action.toLowerCase().startsWith("instruction_executed")){
                    if(StringUtils.isEmpty(instruction_id1)
                            && StringUtils.isEmpty(instruction_command)
                            ){
                        responseBotData.setStatus("ERR.BOT_INSTRUCTION_INCOMPLETE");
                        responseBotData.setMessage("Instruction Id/Command not passed.Tsk,Tsk. Bad Bot");
                        responseEntity = new ResponseEntity<>(responseBotData, HttpStatus.BAD_REQUEST);
                    }else if(StringUtils.isEmpty(instruction_result) ) {
                        responseBotData.setStatus("ERR.BOT_INSTRUCTION_INCOMPLETE");
                        responseBotData.setMessage("Instruction Command Results not passed.Tsk,Tsk. Bad Bot");
                        responseEntity = new ResponseEntity<>(responseBotData, HttpStatus.BAD_REQUEST);
                    }else {
                        HiveBot hiveBot = reportingService.markInstructionCompleted(
                                botData,
                                Long.parseLong(instruction_id1),
                                instruction_command,
                                instruction_result,
                                getEnumSaveOperations(action)
                        );
                        responseBotData = BotClientDataMapper.dumpToJSON(hiveBot, responseBotData, true);
                        responseBotData.setMessage("Ok " +
                                botData.getHiveBotId() + ". Marked Instruction as Completed. Check remaining instructions."
                        );

                        logger.info("\t+" + botData.getHiveBotId() + ":  $  Reporting <EXECUTED: " +
                                "Id:" + instruction_id1 +
                                ", Command:" + instruction_command +
                                ", Result:" + instruction_result + ">");
                        responseBotData.setStatus("ACK");
                        responseEntity = new ResponseEntity<>(responseBotData, HttpStatus.ACCEPTED);
                    }
                    */
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
