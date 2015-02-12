package com.lge.lgreplaymaker.event.inputevent;

import com.lge.lgreplaymaker.event.*;
import java.lang.System;
import java.lang.String;
import java.util.*;
import java.time.LocalDateTime;

public class KeyInfo extends Info {

    String keyCode = "0";
    String keyAction = "";

    public KeyInfo() {
        infoType = "Key";        
    }

    public KeyInfo(String keyCode, String keyAction) {
        infoType = "Key";
        this.keyCode = keyCode;
        this.keyAction = keyAction;        
    }

    public String toString() {
        return "[" + infoType + "][" + keyCode + "|" + keyAction + "]";
        //[Key][3|down]
    }    
}
