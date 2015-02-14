package com.lge.lgreplaymaker.event;

import java.lang.System;
import java.lang.String;
import java.util.*;
import java.time.LocalDateTime;

public class Info {
    public static final String TOUCH_TYPE = "Touch"; 
    public static final String KEY_TYPE = "Key"; 
    public static final String ORIENTATION_TYPE = "Orientation";
    public static final String ACTIVITY_TYPE = "Activity"; 

    public String infoType = "";

    public Info() {
    }

    public String toString() {
        return "[" + infoType + "]";
    }    
}
