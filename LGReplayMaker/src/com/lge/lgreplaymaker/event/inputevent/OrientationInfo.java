package com.lge.lgreplaymaker.event.inputevent; 

import com.lge.lgreplaymaker.event.*;

public class OrientationInfo extends Info {

    public static final String ACTION_PORT = "Port";
    public static final String ACTION_LAND = "Land";

    public String action = "";    

    public OrientationInfo() {
        infoType = Info.ORIENTATION_TYPE;
    }

    public OrientationInfo(String action) {
        infoType = Info.ORIENTATION_TYPE;
        this.action = action;
    }

    public String toString() {
        return "[" + infoType + "][" + action + "]";
        //[Orientation][Land]
    }
}
