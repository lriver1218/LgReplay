package com.lge.lgreplaymaker.event.inputevent;

import com.lge.lgreplaymaker.event.*;

public class ActivityInfo extends Info {

    String act ="";
    String cat = "";
    String flg = "";    
    String cmp = "";

    public ActivityInfo() {
        super.infoType = Info.ACTIVITY_TYPE;
    }

    public ActivityInfo(String act, String cat, String flg, String cmp) {
        super.infoType = Info.ACTIVITY_TYPE;
        this.act = act;
        this.cat = cat;
        this.flg = flg;
        this.cmp = cmp;
    }

    public String toString() {
        return "[" + infoType + "][" + act + "|" + cat + "|" + flg + "|" + cmp + "]";
    }
}
