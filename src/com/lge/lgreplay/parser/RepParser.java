package com.lge.lgreplay.parser;

import android.content.pm.ActivityInfo;
import android.view.KeyEvent;

import com.lge.lgreplay.TimeInfo;
import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.event.EventKey;
import com.lge.lgreplay.event.EventOrientation;
import com.lge.lgreplay.event.EventSleep;
import com.lge.lgreplay.event.EventTouch;
import com.lge.lgreplay.event.EventActivity;

import java.io.*;
import java.util.*;

import android.os.AsyncTask;
import android.util.Log;

public class RepParser {
    static final String TAG = "RepParser";
    static final boolean debug = false;

    static final String TOUCH_KEYWORD = "[IE][Touch]";
    static final String KEY_KEYWORD = "[IE][Key]";
    static final String ORIENTATION_KEYWORD = "[IE][Orientation]";
    static final String ACTIVITY_KEYWORD = "[IE][Activity]";

    public LinkedList<Event> parseFileToList(File file) {
        LinkedList<Event> list = new LinkedList<Event>();

        /*Log.d(TAG, "called parseFileToList");
        ParsingTask parsingTask = new ParsingTask();
        File [] files = new File[1];
        files[0] = file;
        parsingTask.execute(files);
        */
        FileReader in = null; 

        try {
            BufferedReader br =  new BufferedReader(in = new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                Event event = null;
                event = parse(line);
                if (event != null) {
                    list.add(event);
                }
            }
            in.close();
        } catch( IOException e){}
        
        // TODO for test
        /*TimeInfo time = new TimeInfo();
        time.set(274, 35, 12, 14, 26, 1);
        list.add(new EventTouch(985, 1330, EventTouch.ACTION_DOWN, time));
        list.add(new EventSleep(250));
        list.add(new EventTouch(998, 1325, EventTouch.ACTION_UP, time));
        list.add(new EventSleep(2000));

        list.add(new EventTouch(633, 1129, EventTouch.ACTION_DOWN, time));
        list.add(new EventSleep(200));
        list.add(new EventTouch(633, 1129, EventTouch.ACTION_UP, time));
        list.add(new EventSleep(1000));

        list.add(new EventTouch(580, 861, EventTouch.ACTION_DOWN, time));
        list.add(new EventSleep(100));
        list.add(new EventTouch(580, 861, EventTouch.ACTION_UP, time));
        list.add(new EventSleep(2000));

        list.add(new EventOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, time));
        list.add(new EventSleep(3000));
        list.add(new EventOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, time));
        list.add(new EventSleep(2000));

        list.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_DOWN, 0, time));
        list.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_UP, 0, time));
        list.add(new EventSleep(1000));
        list.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_DOWN, 0, time));
        list.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_UP, 0, time));*/

        return list;
    }    

    public Event parse(String line) {
        Event event = null;
        if (line.contains(TOUCH_KEYWORD)) {
            event = parseTouchEvent(line);
        } else if (line.contains(KEY_KEYWORD)) {        
            event = parseKeyEvent(line);
        } else if (line.contains(ORIENTATION_KEYWORD)) {
            event = parseOrientationEvent(line);
        } else if (line.contains(ACTIVITY_KEYWORD)) {
            event = parseActivityEvent(line);
        }

        return event;
    }

    //[01-27 10:20:24.463]
    private TimeInfo parseTime(String timeLog) {
        TimeInfo  time = new TimeInfo();
        if ((timeLog != null) && (!timeLog.equals(""))) {
            timeLog = timeLog.replaceAll("(\\[|\\])", "");
            String str[] = timeLog.split("(-| |:|\\.)");
            int num[] = new int [str.length];
            for (int i = 0; i < str.length ; i++) {
                str[i] = str[i].trim();
                num[i] = Integer.valueOf(str[i]);
            }            
            time.set(num[5], num[4], num[3], num[2], num[1], num[0]);
	}

        return time;
    }

    //[01-26 14:12:35.274][IE][Touch][0|down|695|2488]
    private Event parseTouchEvent(String logLine) {
        int x, y, action = 0;
        TimeInfo time;

        String infoStr[] = logLine.split("\\[IE\\]\\[Touch\\]\\[");
        time = parseTime(infoStr[0]);

        String infoStr2[] = infoStr[1].split("(\\[|\\||\\])");

        for (int i = 0; i < infoStr2.length ; i++) {
                infoStr2[i] = infoStr2[i].trim();
        }
        x = Integer.valueOf(infoStr2[2]);
        y = Integer.valueOf(infoStr2[3]);
        
        if (infoStr2[1].equals("down"))  {
            action = EventTouch.ACTION_DOWN;
        } else if (infoStr2[1].equals("up"))  {
            action = EventTouch.ACTION_UP;
        }        

        if (debug) {
            Log.d(TAG, "TouchEvent time:" + time +" x:" + x + " y:" + y + " action:" + action);
        }

        Event event = new EventTouch(x, y, action, time);

        return event;
    }

    //[01-26 14:12:35.274][IE][Key][3|down]
    private Event parseKeyEvent(String logLine) {
        int keyCode, keyAction = 0;
        TimeInfo time;

        String infoStr[] = logLine.split("\\[IE\\]\\[Key\\]\\[");
        time = parseTime(infoStr[0]);

        String infoStr2[] = infoStr[1].split("(\\[|\\||\\])");

        for (int i = 0; i < infoStr2.length ; i++) {
                infoStr2[i] = infoStr2[i].trim();
        }

        keyCode = Integer.valueOf(infoStr2[0]);

        if (infoStr2[1].equals("down"))  {
            keyAction = EventKey.ACTION_DOWN;
        } else if (infoStr2[1].equals("up"))  {
            keyAction = EventKey.ACTION_UP;
        }        

        if (debug) {
            Log.d(TAG, "KeyEvent time:" + time +" keyCode:" + keyCode + " keyAction:" + keyAction);
        }

        Event event  = new EventKey(keyCode, keyAction, 0, time);
        
        return event;
    }

    //[01-26 14:12:35.274][IE][Orientation][Land]
    private Event parseOrientationEvent(String logLine) {
        int action = 0;
        TimeInfo time;

        String infoStr[] = logLine.split("\\[IE\\]\\[Orientation\\]\\[");
        time = parseTime(infoStr[0]);

        String infoStr2[] = infoStr[1].split("\\]");
        infoStr2[0] = infoStr2[0].trim();

        if (infoStr2[0].equals("Land"))  {
            action = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else if (infoStr2[0].equals("Port"))  {
            action = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        if (debug) {
            Log.d(TAG, "OrientationEvent action:" + action);
        }

        Event event  = new EventOrientation(action, time);
        return event;
    }

    //[02-14 17:09:01.954][IE][Activity][android.intent.action.MAIN|android.intent.category.LAUNCHER|0x10200000|com.android.contacts/.activities.DialtactsActivity]
    private Event parseActivityEvent(String logLine) {        
        TimeInfo time;

        String infoStr[] = logLine.split("\\[IE\\]\\[Activity\\]\\[");
        time = parseTime(infoStr[0]);

        String infoStr2[] = infoStr[1].split("(\\[|\\||\\])");

        for (int i = 0; i < infoStr2.length ; i++) {
                infoStr2[i] = infoStr2[i].trim();
                if (infoStr2[i].equals("null")) {
                    infoStr2[i] = "";
                }
        }

        if (debug) {
            Log.d(TAG, "ActivityEvent time:" + time +" act:" + infoStr2[0] + " dat:" + infoStr2[1] + " cat:" + infoStr2[2] + " flg:" + infoStr2[3] + " cmp:" + infoStr2[4]);
        }

        Event event  = new EventActivity(infoStr2[0], infoStr2[1], infoStr2[2], infoStr2[3], infoStr2[4], time);
        
        return event;
    }

    class ParsingTask extends AsyncTask<File , Long, LinkedList<Event>> {
        @Override
        protected LinkedList<Event> doInBackground(File... file) {
            LinkedList<Event> list= new LinkedList<Event>();
            FileReader in = null;            

                try {
                    BufferedReader br =  new BufferedReader(in = new FileReader(file[0]));
                    String line;

                    while ((line = br.readLine()) != null) {
                        Event event = null;
                        event = parse(line);
                        if (event != null) {
                            list.add(event);
                        }
                    }
                    in.close();
                } catch( IOException e){}
            return list;
        }

        @Override
        protected void	onProgressUpdate(Long... values) {
            //To-Do
            //update progress bar
        }

        @Override
        protected void onPostExecute(LinkedList<Event> eventList) {
            //To-Do
            //Show toast and call callback function
            Log.d(TAG, "onPostExecute");
        }
    }
}