package com.oom.hive.central.utils;

import com.oom.hive.central.repository.model.HiveBot;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class EuropaceAirconIRMapper implements  AirconIRMapper {


    public String prevRawIRDataPack=null;

    public HiveBot enrichDataSet(HiveBot hiveBot){

        //Lets do this later

       /* String ir_raw_data = hiveBot.getDataMap().get("IRemoteData");
        ir_raw_data=ir_raw_data.replaceAll(" ","");

        System.err.println(ir_raw_data);*/

        /*for(Map.Entry<String,String> dMap = hiveBot.getDataMap().entrySet()){

        }*/




        /*Set<BotData> botDataToAdd = new HashSet<BotData>();
        hiveBot.getDataSet().forEach(
                botData -> {
                    if(botData.getDataKey().equals("IRemoteData")){
                        botDataToAdd.addAll(mapIRData(botData.getDataValue()));
                    }
                }
        );

        if(botDataToAdd!=null){
            hiveBot.getDataSet().addAll(botDataToAdd);
        }*/
        return hiveBot;
    }

    /*
    private Set<BotData> mapIRData(String rawIRDataPack){
        Set<BotData> irData = new HashSet<BotData>();


        rawIRDataPack = rawIRDataPack.replace(" ","");

        if(prevRawIRDataPack!=null){
            //Debug Data
            int sIndex = 0;
            char[] _c_prevRawIRDataPack = prevRawIRDataPack.toCharArray();
            char[] _c_rawIRDataPack = rawIRDataPack.toCharArray();
            StringBuffer diffData = new StringBuffer();
            while (sIndex < _c_prevRawIRDataPack.length && sIndex < _c_rawIRDataPack.length ){
                if(_c_prevRawIRDataPack[sIndex] == _c_rawIRDataPack[sIndex]){
                    diffData.append(_c_prevRawIRDataPack[' ']);
                }else if(_c_prevRawIRDataPack[sIndex] > _c_rawIRDataPack[sIndex]){
                    diffData.append('+');
                }else if(_c_prevRawIRDataPack[sIndex] <_c_rawIRDataPack[sIndex]){
                    diffData.append('-');
                }
                sIndex++;
            }
            System.out.println("IR_CHECK:OLD  " + prevRawIRDataPack);
            System.out.println("IR_CHECK:NEW  " + rawIRDataPack);
            System.out.println("IR_CHECK:DIF  " + diffData);

            //TODO
            irData.add(new BotData("AirconPower","ON"));
            irData.add(new BotData("AirconTemp","100"));
            irData.add(new BotData("AirconMode","NA"));
            irData.add(new BotData("AirconName","Europace"));
        }



        prevRawIRDataPack = rawIRDataPack;
        return irData;
    }
    */

/*

def interpetCommand(theCommand):
    theCommandInterpret =""
    if(len(theCommand) < 120): # i dont understand this, and yes has to be 2 Framse, note its string length not mils
        return ".NOT_UNDERSTOOD.Len" + str(len(theCommand))

    wx_command_power = "UKN"
    wx_command_temp = "UKN"
    wx_command_mode = "UKN"

    #The ON OFF Mode
    if(theCommand[4]=="A"):
        wx_command_power=  "ON"
    elif (theCommand[4] == " "):
        wx_command_power = "OFF"


    #The Temperature settings
    if (theCommand[9:13] == "    "):
        wx_command_temp =  "16.0"
    elif (theCommand[9:13] == "A   "):
        wx_command_temp =  "17.0"
    elif (theCommand[9:13] == " A  "):
        wx_command_temp =  "18.0"
    elif (theCommand[9:13] == "AA  "):
        wx_command_temp =  "19.0"
    elif (theCommand[9:13] == "  A "):
        wx_command_temp =  "20.0"
    elif (theCommand[9:13] == "A A "):
        wx_command_temp =  "21.0"
    elif (theCommand[9:13] == " AA "):
        wx_command_temp =  "22.0"
    elif (theCommand[9:13] == "AAA "):
        wx_command_temp =  "23.0"
    elif (theCommand[9:13] == "   A"):
        wx_command_temp =  "24.0"
    elif (theCommand[9:13] == "A  A"):
        wx_command_temp =  "25.0"
    elif (theCommand[9:13] == " A A"):
        wx_command_temp =  "26.0"
    elif (theCommand[9:13] == "AA A"):
        wx_command_temp =  "27.0"
    elif (theCommand[9:13] == "  AA"):
        wx_command_temp =  "28.0"
    elif (theCommand[9:13] == "A AA"):
        wx_command_temp =  "29.0"
    elif (theCommand[9:13] == " AAA"):
        wx_command_temp =  "30.0"



    # The Modes settings
    if (theCommand[1:5] == "   A"):
        wx_command_mode =  "AUTO"
    elif (theCommand[1:5] == "A  A"):
        wx_command_mode =  "COOL"
    elif (theCommand[1:5] == " A A"):
        wx_command_mode = "DRY"
    elif (theCommand[1:5] == "AA A"):
        wx_command_mode = "FAN"
    elif (theCommand[1:5] == "  AA"):
        wx_command_mode = "HEAT"



    # The FAN Speed Settings
    # TODO but Differs from Mode to Mode, expected watch space 127 to end but chances of MisAlignment.


    theCommandInterpret = theCommandInterpret + wx_command_power + wx_command_temp + wx_command_mode


    paramDataArray = {}
    paramDataArray['power'] = wx_command_power
    paramDataArray['temp'] = wx_command_temp
    paramDataArray['mode'] = wx_command_mode
    webserviceInvokeAction(config, "aircon_ir_input", paramDataArray)

    return theCommandInterpret

 */
}
