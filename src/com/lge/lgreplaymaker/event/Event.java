package com.lge.lgreplaymaker.event;

import java.lang.System;
import java.lang.String;
import java.util.*;
import java.time.LocalDateTime;

public class Event {

    public LocalDateTime time = null; 
    public String logFormattedTime = "";
    public String eventType = "";
    public Info info;

    public Event() {
        info = new Info();
    }

    public String toString() {        
        return "[" + logFormattedTime + "]" + "[" + eventType + "]" + info.toString();
    }

    public void convertLogTimeToLocalDateTime() {
        if ((logFormattedTime != null) && (!logFormattedTime.equals(""))) {
            //System.out.println(logFormattedTime);
            String str[] = logFormattedTime.split("(-| |:|\\.)");
            int num[] = new int [str.length];
            for (int i = 0; i < str.length ; i++) {
                str[i] = str[i].trim();
                num[i] = Integer.valueOf(str[i]);
                                
                //System.out.println("[" + i + "] " + num[i] );
            }
            time = LocalDateTime.of(2015, num[0], num[1], num[2], num[3], num[4], num[5] *1000 * 1000);  //The year is not neccessory.
            //System.out.println(time);
        }

         //01-27 10:20:24.463
         //public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond)
         //LocalDateTime mLocalDateTime = LocalDateTime.of(2014, 1, 16, 2, 30, 5, 7*1000 * 1000);        
        //TreeMap <LocalDateTime, Integer>  tm = new TreeMap <LocalDateTime, Integer> ();          
    }
}
