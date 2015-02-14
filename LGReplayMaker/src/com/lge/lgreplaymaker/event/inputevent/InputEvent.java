package com.lge.lgreplaymaker.event.inputevent;

import com.lge.lgreplaymaker.event.*;
import java.lang.System;
import java.lang.String;
import java.util.*;
import java.time.LocalDateTime;

public class InputEvent extends Event{    

    public InputEvent() {
        eventType = Event.INPUT_EVENT_TYPE;
    }

    public InputEvent(String logFormattedTime) {
        eventType = "IE";
        this.logFormattedTime = logFormattedTime;
    }

    public String toString() {
        return super.toString();
    }    
}
