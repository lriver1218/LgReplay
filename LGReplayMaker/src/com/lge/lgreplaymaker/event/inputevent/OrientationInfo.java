package com.lge.lgreplaymaker.event.inputevent; 

import com.lge.lgreplaymaker.event.*;
import java.lang.System;
import java.lang.String;
import java.util.*;
import java.time.LocalDateTime;

public class OrientationInfo extends Info {

    public static final String ACTION_PORT = "Port";
    public static final String ACTION_LAND = "Land";

    public String action = "";    

    public OrientationInfo() {
        infoType = "Orientation";
    }

    public OrientationInfo(String action) {
        infoType = "Orientation";
        this.action = action;
    }

    public String toString() {
        return "[" + infoType + "][" + action + "]";
        //[Orientation][Land]
    }
}
