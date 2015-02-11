
package com.lge.lgreplay.parser;

import android.content.pm.ActivityInfo;
import android.view.KeyEvent;

import com.lge.lgreplay.TimeInfo;
import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.event.EventKey;
import com.lge.lgreplay.event.EventOrientation;
import com.lge.lgreplay.event.EventSleep;
import com.lge.lgreplay.event.EventTouch;

import java.io.File;
import java.util.LinkedList;

public class RepParser {
    public LinkedList<Event> parseFileToList(File file) {
        LinkedList<Event> list = new LinkedList<Event>();

        // TODO for test
        TimeInfo time = new TimeInfo();
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
        list.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_UP, 0, time));

        return list;
    }
}
