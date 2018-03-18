package com.oom.hive.central;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.TimeZone;


public class AppSettings {

    public final static String TSTAMP_FORMAT = "dd-MMM HH:mm";
    public final static SimpleDateFormat sDateFormat = new SimpleDateFormat(TSTAMP_FORMAT);
    //public final static TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Singapore");



    public static String formatDate(Date inDate){
        return sDateFormat.format(inDate);
    }
    public static Date parseDate(String inDate) throws ParseException {
        return sDateFormat.parse(inDate);
    }

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
        StringBuffer stringBuffer = new StringBuffer();
        hsOperations.forEach(oper->{
            switch(oper){
                case ADD_INSTRUCTIONS : stringBuffer.append("ADD_INSTRUCTIONS"); break;
                case SET_INSTRUCTIONS : stringBuffer.append("SET_INSTRUCTIONS"); break;
                case CLEAR_INSTRUCTIONS : stringBuffer.append("CLEAR_INSTRUCTIONS"); break;
                case ADD_DATAMAP : stringBuffer.append("ADD_DATAMAP"); break;
                case SET_DATAMAP : stringBuffer.append("SET_DATAMAP"); break;
                case CLEAR_DATAMAP : stringBuffer.append("CLEAR_DATAMAP"); break;
                case SAVE_INFO : stringBuffer.append("SAVE_INFO"); break;
                case EVENTLOG_DATAMAP : stringBuffer.append("EVENTLOG_DATAMAP"); break;
                case BOT_IS_ALIVE : stringBuffer.append("BOT_IS_ALIVE"); break;
                default: break;
            }
        });
        return stringBuffer.toString();
    }
}
