
package com.lge.lgreplay.event;

import com.lge.lgreplay.TimeInfo;

public class EventOrientation extends Event {

    int mOrientation;

    public EventOrientation(int orientation, TimeInfo time) {
        setType(TYPE_ORIENTATION);
        setTime(time);

        mOrientation = orientation;
    }

    public int getOrientation() {
        return mOrientation;
    }
 
    public void setOrientation(int orientation) {
    	mOrientation = orientation;
    }
}
