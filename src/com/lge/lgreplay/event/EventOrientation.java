
package com.lge.lgreplay.event;

public class EventOrientation extends Event {

    int mOrientation;

    public EventOrientation(int orientation) {
        setType(TYPE_ORIENTATION);

        mOrientation = orientation;
    }

    public int getOrientation() {
        return mOrientation;
    }
 
    public void setOrientation(int orientation) {
    	mOrientation = orientation;
    }
}
