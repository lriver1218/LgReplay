package com.lge.lgreplaymaker;

import com.lge.lgreplaymaker.event.*;
import com.lge.lgreplaymaker.parser.*;
import java.util.*;
import java.time.LocalDateTime;

public class LogExtractor
{    
    TreeMap <LocalDateTime, Event>  eventTreeMap;

    EventParser inputEventParser;

    public LogExtractor() {
        eventTreeMap = new TreeMap <LocalDateTime, Event> ();
        inputEventParser = new InputEventParser();        
    }

    public void extract(String logLine) {
        Event event = null;
        event = inputEventParser.parse(logLine);

        if (event != null) {
            event.convertLogTimeToLocalDateTime();
            eventTreeMap.put(event.time, event);
        }        
    }

    public TreeMap <LocalDateTime, Event>  getEventTreeMap() {
        return eventTreeMap;
    }        
}


