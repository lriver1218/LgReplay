
package com.lge.lgreplay.event;

public class EventKey extends Event {
	public static final int ACTION_DOWN = 0;
    public static final int ACTION_UP = 1;
    public static final int ACTION_CANCEL = 2;
    
    int mKey;
    int mAction;
    int mRepeat;

    public EventKey(int key, int action, int repeat) {
        setType(TYPE_KEY);

        mKey = key;
        mAction = action;
        mRepeat = repeat;
    }

    public int getKey() {
        return mKey;
    }
    
    public int getAction() {
    	return mAction;
    }
    
    public int getRepeat() {
    	return mRepeat;
    }

    public void setKey(int key) {
        mKey = key;
    }
    
    public void setAction(int action) {
    	mAction = action;
    }
    
    public void setRepeat(int repeat) {
    	mRepeat = repeat;
    }
}
