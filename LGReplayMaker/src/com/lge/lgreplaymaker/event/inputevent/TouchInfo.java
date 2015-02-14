package com.lge.lgreplaymaker.event.inputevent;

import com.lge.lgreplaymaker.event.*;

enum Action {down, up}

public class TouchInfo extends Info {

    String id ="0";
    String action = "";
    String x = "0";
    String y = "0";

    public TouchInfo() {
        super.infoType = Info.TOUCH_TYPE;
    }

    public TouchInfo(String id, String action, String x, String y) {
        infoType = Info.TOUCH_TYPE;
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
