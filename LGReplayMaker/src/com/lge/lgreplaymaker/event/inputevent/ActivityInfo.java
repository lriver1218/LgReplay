package com.lge.lgreplaymaker.event.inputevent;

import com.lge.lgreplaymaker.event.*;

public class ActivityInfo extends Info {

    String act ="";
    String dat ="";
    String cat = "";
    String flg = "";    
    String cmp = "";
    String extra = "";

    public ActivityInfo() {
        super.infoType = Info.ACTIVITY_TYPE;
    }

    public ActivityInfo(String act, String dat, String cat, String flg, String cmp, String extra) {
        super.infoType = Info.ACTIVITY_TYPE;
        this.act = act;
        this.dat = dat;
        this.cat = cat;
        this.flg = flg;
        this.cmp = cmp;
        this.extra = extra;
    }

    public String toString() {
        return "[" + infoType + "][" + act + "|" + dat + "|" +cat + "|" + flg + "|" + cmp + "|" + extra + "]";
    }
}
