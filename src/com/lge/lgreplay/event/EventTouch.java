
package com.lge.lgreplay.event;

import com.lge.lgreplay.TimeInfo;

import java.util.ArrayList;
import android.graphics.Point;


public class EventTouch extends Event {
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_UP = 1;
    public static final int ACTION_MOVE = 2;
    public static final int ACTION_CANCEL = 3;
    public static final int ACTION_OUTSIDE = 4;

    private int mAction;

    private int[] mX;
    private int[] mY;

    ArrayList <Point> movedPoints = null;

    public EventTouch(int x, int y, int action, TimeInfo time) {
        setType(TYPE_TOUCH);
        setTime(time);

        mX = new int[1];
        mY = new int[1];

        mX[0] = x;
        mY[0] = y;

        mAction = action;
    }

    public EventTouch(int x, int y, int action, TimeInfo time, ArrayList <Point> points) {
        setType(TYPE_TOUCH);
        setTime(time);

        mX = new int[1];
        mY = new int[1];

        mX[0] = x;
        mY[0] = y;

        mAction = action;

        this.movedPoints = points;
    }

    public float getX() {
        return mX[0];
    }

    public float getX(int i) {
        return mX[i];
    }

    public float getY() {
        return mY[0];
    }

    public float getY(int i) {
        return mY[i];
    }

    public int getAction() {
        return mAction;
    }

    public ArrayList <Point> getMovedPoints() {
        return movedPoints;
    }
}
