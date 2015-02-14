package com.lge.lgreplaymaker.event.inputevent;

import com.lge.lgreplaymaker.event.*;

public class KeyInfo extends Info {

    String keyCode = "0";
    String keyAction = "";

    public KeyInfo() {
        infoType = Info.KEY_TYPE;        
    }

    public KeyInfo(String keyCode, String keyAction) {
        infoType = Info.KEY_TYPE;
        this.keyCode = keyCode;
        this.keyAction = keyAction;        
    }

    public String toString() {
        return "[" + infoType + "][" + keyCode + "|" + keyAction + "]";
        //[Key][3|down]
    }    
}
