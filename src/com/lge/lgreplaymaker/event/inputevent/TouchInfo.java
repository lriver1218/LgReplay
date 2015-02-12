package com.lge.lgreplaymaker.event.inputevent;

import com.lge.lgreplaymaker.event.*;
import java.lang.System;
import java.lang.String;
import java.util.*;
import java.time.LocalDateTime;

enum Action {down, up}

public class TouchInfo extends Info {

    String id ="0";
    String action = "";
    String x = "0";
    String y = "0";

    public TouchInfo() {
        super.infoType = "Touch";
    }

    public TouchInfo(String id, String action, String x, String y) {
        infoType = "Touch";
        this.id = id;
        this.action = action;
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "[" + infoType + "][" + id + "|" + action + "|" + x + "|" + y + "]";
        //[Touch][0|down|695|2488]
    }
}
