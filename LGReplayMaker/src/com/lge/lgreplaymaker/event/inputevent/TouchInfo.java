package com.lge.lgreplaymaker.event.inputevent;

import com.lge.lgreplaymaker.event.*;

import java.util.ArrayList;
import java.awt.Point;

enum Action {down, up}

public class TouchInfo extends Info {

    String id ="0";
    String action = "";
    String x = "0";
    String y = "0";
    ArrayList <Point> movedPoints = null;

    public TouchInfo() {
        super.infoType = Info.TOUCH_TYPE;
        movedPoints = new ArrayList<Point>();
    }

    public TouchInfo(String id, String action, String x, String y) {
        infoType = Info.TOUCH_TYPE;
        this.id = id;
        this.action = action;
        this.x = x;
        this.y = y;
    }

    public TouchInfo(String id, String action, String x, String y, ArrayList <Point>points) {
        infoType = Info.TOUCH_TYPE;
        this.id = id;
        this.action = action;
        this.x = x;
        this.y = y;
        movedPoints = new ArrayList<Point>();
        if (points != null) {
            this.movedPoints = points;
        }       
    }

    public String toString() {
        String movedString = "";
        if (movedPoints != null && movedPoints.size() > 0) {
            movedString = "(";
            for (int i = 0; i < movedPoints.size(); i++) {
                Point p = movedPoints.get(i);
                if (i > 0) {
                    movedString = movedString + "|";
                }
                movedString = movedString + p.x  + "," + p.y;
            }
            movedString = movedString + ")";
        }
        return "[" + infoType + "][" + id + "|" + action + "|" + x + "|" + y + movedString  + "]";
        //[Touch][0|down|695|2488]
    }
}
