package com.oom.hive.central.model.types;

import java.util.HashMap;
import java.util.Objects;

public class HiveBotDataType {

    public static String SensorData = new String("SensorData");
    public static String ExecuteInstruction = new String("ExecuteInstruction");
    public static String BootupHivebot = new String("BootupHivebot");
    public static String CatchupPostBootup = new String("CatchupPostBootup");
    public static String UpdateFunctions = new String("UpdateFunctions");
    public static String InstructionCompleted = new String("InstructionCompleted");
    public static String InstructionFailed = new String("InstructionFailed");
    public static String HeartBeat = new String("HeartBeat");


/*

    static private HashMap <String,HiveBotDataType> registeredDataType = new HashMap<String,HiveBotDataType>();
    public  static HiveBotDataType getDataType(String dataTypeStr){
        return registeredDataType.get(dataTypeStr);
    }

    private String dataTypeVal  = null;
    private HiveBotDataType(String dataTypeStr){
        this.dataTypeVal = dataTypeStr;
        registeredDataType.put(dataTypeStr, this);
    }
    public String getStringValue(){
        return dataTypeVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HiveBotDataType that = (HiveBotDataType) o;
        return Objects.equals(dataTypeVal, that.dataTypeVal);
    }

    @Override
    public int hashCode() {

        return Objects.hash(dataTypeVal);
    }*/
}
