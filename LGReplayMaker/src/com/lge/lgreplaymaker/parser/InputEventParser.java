package com.lge.lgreplaymaker.parser;

import com.lge.lgreplaymaker.event.*;
import com.lge.lgreplaymaker.event.inputevent.*;

import java.io.*;
import java.lang.System;
import java.lang.String;
import java.lang.Object;
import java.util.*;
import java.time.LocalDateTime;

public class InputEventParser implements EventParser
{
    static final boolean debug = false; 
    
    static final String TOUCH_DOWN_KEYWORD = "[Touch] 1 finger pressed";
    static final String TOUCH_UP_KEYWORD = "[Touch] touch_release[ ] ";    
    static final String KEY_KEYWORD = "PhoneWindowManagerEx: interceptKeyTq";
    static final String ORIENTATION_KEYWORD = "InputReader: Device reconfigured: ";

    InputEvent event = null;

    TreeMap <LocalDateTime, Event>  eventTreeMap;   //for test

    public InputEventParser() {
        
        if (debug) { //for test
            eventTreeMap = new TreeMap <LocalDateTime, Event> ();
            
            parse("<6>[ 1303.556382 / 01-27 10:20:24.463] [Touch] 1 finger pressed : <0> x[ 670] y[2493] z[ 50]");
            parse("<6>[ 1303.600001 / 01-29 10:21:14.503] [Touch] touch_release[ ] : <3> x[ 672] y[2495]");
            parse("02-27 16:02:41.585  3595  4576 D PhoneWindowManagerEx: interceptKeyTq keycode=13 down=true interactive=true  policyFlags =2b000002 injected=true keyguardActive=false");
            parse("03-28 16:02:41.651  3595  4577 D PhoneWindowManagerEx: interceptKeyTq keycode=3 down=false interactive=true policyFlags =2b000002 injected=true keyguardActive=false");
            parse("04-13 23:55:45.360  1161  1463 I InputReader: Device reconfigured: id=4, name='touch_dev', size 1080x1920, orientation 0, mode 1, display id 0");

            Set <LocalDateTime> keySet = eventTreeMap.keySet();
            Iterator <LocalDateTime> iterator = keySet.iterator();            
            while (iterator.hasNext()) {
                LocalDateTime time = iterator.next();
                System.out.println(eventTreeMap.get(time));
            }
        }
    }

    public Event parse(String logLine) {
        event = null;

        if (logLine.contains(TOUCH_DOWN_KEYWORD)) {
            event = parseTouchEvent(logLine);
        } else if (logLine.contains(TOUCH_UP_KEYWORD)) {
            event = parseTouchEvent(logLine);
        } else if (logLine.contains(KEY_KEYWORD)) {
            event = parseKeyEvent(logLine);
        } else if (logLine.contains(ORIENTATION_KEYWORD)) {
            event = parseOrientationEvent(logLine);
        } 

        if (debug && (event != null)) { //for Test
            System.out.println(event.toString());            
        }
        if (debug) {    //for Test
            if (event != null) {
                event.convertLogTimeToLocalDateTime();
                eventTreeMap.put(event.time, event);
            }   
        }

        return event;
    }

    private InputEvent parseTouchEvent(String logLine) {
        event = new InputEvent();

        if (logLine.contains(TOUCH_DOWN_KEYWORD)) {
            String infoStr[] = logLine.split("/");
            String infoStr2[] = infoStr[1].split("(\\] \\[|\\[|\\]|<|>)");

            Info info =  new TouchInfo(infoStr2[3].trim(), "down", infoStr2[5].trim(), infoStr2[7].trim());
            event.logFormattedTime = infoStr2[0].trim();
            event.info = info;            

            /*for (int i = 0; i < infoStr2.length ; i++) {
                infoStr2[i] = infoStr2[i].trim();
                System.out.println(i + "<" + infoStr2[i] + ">");
            }*/
            //String out = "[" + infoStr2[0].trim() + "][IE][Touch][" + infoStr2[3] + "|down|" + infoStr2[5] + "|"  + infoStr2[7] + "]";
            //System.out.println(out);
        } else if (logLine.contains(TOUCH_UP_KEYWORD)) {
            String infoStr[] = logLine.split("/");
            String infoStr2[] = infoStr[1].split("(\\] \\[|\\[|\\]|<|>)");

            Info info =  new TouchInfo(infoStr2[5].trim(), "up", infoStr2[7].trim(), infoStr2[9].trim());
            event.logFormattedTime = infoStr2[0].trim();
            event.info = info;

            /*for (int i = 0; i < infoStr2.length ; i++) {
                infoStr2[i] = infoStr2[i].trim();
                System.out.println(i + "<" + infoStr2[i] + ">");
            }*/
            //String out = "[" + infoStr2[0] + "][IE][Touch][" + infoStr2[5] + "|up|" + infoStr2[7] + "|"  + infoStr2[9] + "]";
            //System.out.println(out);
        }
            return event;
/*            
//--------------------------------------------------------------------------------------------------------------
time, id, x, y
ex>
rep format: [01-27 10:20:24.463][IE][Touch][0|down|670|2493]
log:             <6>[ 1303.556382 / 01-27 10:20:24.463] [Touch] 1 finger pressed : <0> x[ 670] y[2493] z[ 50]
log:             <6>[ 1303.600001 / 01-27 10:20:24.503] [Touch] touch_release[ ] : <0> x[ 672] y[2495]
//--------------------------------------------------------------------------------------------------------------
*/
    }

    private InputEvent parseKeyEvent(String logLine) {
        event = new InputEvent();
        if (logLine.contains(KEY_KEYWORD)) {
            String time = logLine.substring(0, 18);
            String infoStr[] = logLine.split("(interceptKeyTq keycode=| down=| interactive=)");

            for (int i = 0; i < infoStr.length ; i++) {
                infoStr[i] = infoStr[i].trim();              
            }

            if (infoStr[2].equals("true")) {
                infoStr[2] = "down";
            } else {
                infoStr[2] = "up";
            }

            Info info =  new KeyInfo(infoStr[1], infoStr[2]);
            event.logFormattedTime = time.trim();
            event.info = info;            

            //String out = "[" + time + "][IE][Key][" + infoStr[1] + "|" + infoStr[2] + "]";
            //System.out.println(out);
        }
        return event;
/*
//--------------------------------------------------------------------------------------------------------------
time, keycode, action
ex>
rep format: [01-26 14:12:35.274][IE][Key][3|down]
log:             01-27 16:02:41.585  3595  4576 D PhoneWindowManagerEx: interceptKeyTq keycode=3 down=true interactive=true  policyFlags =2b000002 injected=true keyguardActive=false
log:             01-27 16:02:41.651  3595  4577 D PhoneWindowManagerEx: interceptKeyTq keycode=3 down=false interactive=true policyFlags=2b000002 injected=true keyguardActive=false
//--------------------------------------------------------------------------------------------------------------
*/
    }

    private InputEvent parseOrientationEvent(String logLine) {
        event = new InputEvent();
        if (logLine.contains(ORIENTATION_KEYWORD)) {
            String time = logLine.substring(0, 18);
            String infoStr[] = logLine.split("(orientation |, mode|, display id)");

            for (int i = 0; i < infoStr.length ; i++) {
                infoStr[i] = infoStr[i].trim();
            }

            if ((infoStr[1].equals("0"))  || (infoStr[1].equals("2"))) {
                infoStr[1] = OrientationInfo.ACTION_PORT;
            } else {
                infoStr[1] = OrientationInfo.ACTION_LAND;
            }

            Info info =  new OrientationInfo(infoStr[1] );
            event.logFormattedTime = time.trim();
            event.info = info;                        

            //String out = "[" + time + "][IE][Orientation][" + info[1] + "]";
            //System.out.println(out);
        }
        return event;
/*
//--------------------------------------------------------------------------------------------------------------
time, land/Port
ex>
rep format: [01-26 14:12:35.274][IE][Orientation][Land]
log:             01-27 23:55:45.360  1161  1463 I InputReader: Device reconfigured: id=4, name='touch_dev', size 1080x1920, orientation 0, mode 1, display id 0
//--------------------------------------------------------------------------------------------------------------
*/
    }
}


