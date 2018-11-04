package com.oom.hive.central;


import java.util.EnumSet;



public class AppSettings {

    public static final String TSTAMP_FORMAT = "dd-MMM HH:mm";


    public final static String EMPTY_HIVEINSTRUCTION =".none.";



    public enum HiveSaveOperation{
        ADD_INSTRUCTIONS,
        SET_INSTRUCTIONS,
        CLEAR_INSTRUCTIONS,
        ADD_DATAMAP,
        SET_DATAMAP,
        CLEAR_DATAMAP,
        SAVE_INFO,
        EVENTLOG_DATAMAP,
        BOT_IS_ALIVE
    }
    public static String hiveOperationsToString(EnumSet<HiveSaveOperation> hsOperations){
        StringBuilder stringBuilder = new StringBuilder();
        hsOperations.forEach(oper->{
            switch(oper){
                case ADD_INSTRUCTIONS : stringBuilder.append("ADD_INSTRUCTIONS"); break;
                case SET_INSTRUCTIONS : stringBuilder.append("SET_INSTRUCTIONS"); break;
                case CLEAR_INSTRUCTIONS : stringBuilder.append("CLEAR_INSTRUCTIONS"); break;
                case ADD_DATAMAP : stringBuilder.append("ADD_DATAMAP"); break;
                case SET_DATAMAP : stringBuilder.append("SET_DATAMAP"); break;
                case CLEAR_DATAMAP : stringBuilder.append("CLEAR_DATAMAP"); break;
                case SAVE_INFO : stringBuilder.append("SAVE_INFO"); break;
                case EVENTLOG_DATAMAP : stringBuilder.append("EVENTLOG_DATAMAP"); break;
                case BOT_IS_ALIVE : stringBuilder.append("BOT_IS_ALIVE"); break;
                default: break;
            }
        });
        return stringBuilder.toString();
    }
}
