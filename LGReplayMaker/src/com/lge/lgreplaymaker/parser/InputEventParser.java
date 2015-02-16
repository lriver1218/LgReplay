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

    static final boolean isSWKey = true;    //sw key model (has navi bar)

    static final String TOUCH_DOWN_KEYWORD = "[Touch] 1 finger pressed";
    static final String TOUCH_UP_KEYWORD = "[Touch] touch_release[ ] ";    
    static final String KEY_KEYWORD = "PhoneWindowManagerEx: interceptKeyTq";
    static final String ORIENTATION_KEYWORD = "InputReader: Device reconfigured: ";
    static final String ACTIVITY_KEYWORD = "ActivityManager: START u0 {";

    TreeMap <LocalDateTime, Event>  eventTreeMap;   //for test

    HashMap <String, Integer> skipKeyMap;

    public InputEventParser() {
        if (isSWKey) {
            skipKeyMap = new HashMap <String, Integer> ();
            addSkipKeyCode ();
        }

        if (false) { //for test
            eventTreeMap = new TreeMap <LocalDateTime, Event> ();

            parse("02-14 17:05:19.840  1057  1057 I ActivityManager: START u0 {act=android.intent.action.MAIN cat=[android.intent.category.HOME] flg=0x10000000 cmp=com.lge.launcher2/.Launcher} from uid 0 on display 0");
            parse("02-12 10:42:14.555   942  1718 I ActivityManager: START u0 {act=android.intent.action.PICK dat= typ=vnd.android.cursor.dir/track cmp=com.lge.music/.TrackBrowserActivity (has extras)} from uid 10027 on display 0");
            /*
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
            */
        }
        
    }

    // SW touch-key model does not need these keys because of 
    private void addSkipKeyCode () {
        skipKeyMap.put(String.valueOf(KeyCode.KEYCODE_HOME), KeyCode.KEYCODE_HOME);
        skipKeyMap.put(String.valueOf(KeyCode.KEYCODE_BACK), KeyCode.KEYCODE_BACK);
        skipKeyMap.put(String.valueOf(KeyCode.KEYCODE_NOTIFICATION), KeyCode.KEYCODE_NOTIFICATION);
        skipKeyMap.put(String.valueOf(KeyCode.KEYCODE_CHANNEL_DOWN), KeyCode.KEYCODE_CHANNEL_DOWN);
        skipKeyMap.put(String.valueOf(KeyCode.KEYCODE_WINDOW), KeyCode.KEYCODE_WINDOW);
        skipKeyMap.put(String.valueOf(KeyCode.KEYCODE_GUIDE), KeyCode.KEYCODE_GUIDE);
        skipKeyMap.put(String.valueOf(KeyCode.KEYCODE_APP_SWITCH), KeyCode.KEYCODE_APP_SWITCH);
    }

    public Event parse(String logLine) {
        Event event = null;

        if (logLine.contains(TOUCH_DOWN_KEYWORD)) {
            event = parseTouchEvent(logLine);
        } else if (logLine.contains(TOUCH_UP_KEYWORD)) {
            event = parseTouchEvent(logLine);
        } else if (logLine.contains(KEY_KEYWORD)) {
            event = parseKeyEvent(logLine);
        } else if (logLine.contains(ORIENTATION_KEYWORD)) {
            event = parseOrientationEvent(logLine);
        } else if (logLine.contains(ACTIVITY_KEYWORD)) {
            event = parseActivityEvent(logLine);
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

    private Event parseTouchEvent(String logLine) {
        Event event = new InputEvent();
        String x = "0";
        String y = "0";

        if (logLine.contains(TOUCH_DOWN_KEYWORD)) {
            String infoStr[] = logLine.split("/");
            String infoStr2[] = infoStr[1].split("(\\] \\[|\\[|\\]|<|>)");

            x = infoStr2[5].trim();
            y = infoStr2[7].trim();

            Info info =  new TouchInfo(infoStr2[3].trim(), "down", x, y);
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

            x = infoStr2[7].trim();
            y = infoStr2[9].trim();

            Info info =  new TouchInfo(infoStr2[5].trim(), "up", x, y);
            event.logFormattedTime = infoStr2[0].trim();
            event.info = info;

            /*for (int i = 0; i < infoStr2.length ; i++) {
                infoStr2[i] = infoStr2[i].trim();
                System.out.println(i + "<" + infoStr2[i] + ">");
            }*/
            //String out = "[" + infoStr2[0] + "][IE][Touch][" + infoStr2[5] + "|up|" + infoStr2[7] + "|"  + infoStr2[9] + "]";
            //System.out.println(out);
        }

        //check the right number for x, y value
        try {
            if ((Integer.valueOf(x) == null) || (Integer.valueOf(y) == null)) {                
                return null;
            }
        } catch (Exception e) {
            return null;
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

    private Event parseKeyEvent(String logLine) {
        Event event = new InputEvent();
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

            String keyCode = infoStr[1];
            if (isSWKey && skipKeyMap.containsKey(keyCode)) {                
                return null;
            }

            Info info =  new KeyInfo(keyCode, infoStr[2]);
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

    private Event parseOrientationEvent(String logLine) {
        Event event = new InputEvent();
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

    private Event parseActivityEvent(String logLine) {
        Event event = new InputEvent();        
        final String [] infoKeyword = {"act=", "dat=", "cat=", "flg=", "cmp="};
        final String extraKeyword = "has extras";
        String [] infoStrs = new String[5];
        String extra = "null"; 

        if (logLine.contains(ACTIVITY_KEYWORD)) {
            String time = logLine.substring(0, 18);

            if (logLine.contains(extraKeyword)) {
                extra = extraKeyword;
            }

            for (int i = 0 ; i < infoKeyword.length ; i++) {
                if (logLine.contains(infoKeyword[i])) {
                    int start = logLine.indexOf(infoKeyword[i]);
                    String infoStr = logLine.substring(start);
                    String reg = "(" + infoKeyword[i] + "| |})" ;
                    String infoStr2[] = infoStr.split(reg);
                    infoStr2[1] = infoStr2[1].replaceAll("(\\[|\\])", "");
                    infoStrs[i] = infoStr2[1];
                    //System.out.println(infoKeyword[i] + " <" + infoStr2[1] + ">");
                }                
            }

            Info info =  new ActivityInfo(infoStrs[0], infoStrs[1], infoStrs[2], infoStrs[3], infoStrs[4], extra);
            event.logFormattedTime = time.trim();
            event.info = info;
        } 

        return event;
/*            
//--------------------------------------------------------------------------------------------------------------
time, id, x, y
ex>
rep format:  [01-26 14:12:35.274][IE][Activity][android.intent.action.MAIN|android.intent.category.HOME|0x10200000|com.lge.launcher2/.Launcher]
log: 
02-14 17:05:19.840  1057  1057 I ActivityManager: START u0 {act=android.intent.action.MAIN cat=[android.intent.category.HOME] flg=0x10000000 cmp=com.lge.launcher2/.Launcher} from uid 0 on display 0
02-14 17:05:25.920  1057  1416 I ActivityManager: START u0 {act=android.intent.action.MAIN flg=0x10000000 cmp=com.android.settings/.lgesetting.wireless.DataNetworkModePayPopupLGT} from uid 1001 on display 0
02-14 17:08:11.923  1057  1315 I ActivityManager: START u0 {act=android.intent.action.MAIN cat=[android.intent.category.HOME] flg=0x10200000 cmp=com.lge.launcher2/.Launcher} from uid 1000 on display 0
02-14 17:09:01.954  1057  1840 I ActivityManager: START u0 {act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] flg=0x10200000 cmp=com.android.contacts/.activities.DialtactsActivity bnds=[0,2128][288,2392]} from uid 10028 on display 0
02-14 17:09:29.963  1057  1073 I ActivityManager: START u0 {act=android.intent.action.MAIN cat=[android.intent.category.LAUNCHER] flg=0x10200000 cmp=com.android.contacts/.activities.PeopleActivity bnds=[288,2128][576,2392]} from uid 10028 on display 0
02-14 17:09:35.221  1057  1441 I ActivityManager: START u0 {dat=content://com.android.contacts/groups/3 cmp=com.android.contacts/.activities.GroupDetailActivity (has extras)} from uid 10018 on display 0
02-14 17:09:36.857  1057  1315 I ActivityManager: START u0 {act=android.intent.action.MAIN cat=[android.intent.category.HOME] flg=0x10200000 cmp=com.lge.launcher2/.Launcher} from uid 1000 on displ
//--------------------------------------------------------------------------------------------------------------
*/
    }

}

class KeyCode {
    public static final int KEYCODE_HOME            = 3;
    public static final int KEYCODE_BACK            = 4;
    public static final int KEYCODE_NOTIFICATION    = 83;
    public static final int KEYCODE_CHANNEL_DOWN    = 167;
    public static final int KEYCODE_WINDOW          = 171;
    public static final int KEYCODE_GUIDE           = 172;
    public static final int KEYCODE_APP_SWITCH      = 187;
}
