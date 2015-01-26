
package com.lge.lgreplay.event;

public abstract class Event {
    public static final int TYPE_SLEEP = 0;
    public static final int TYPE_TOUCH = 1;
    public static final int TYPE_KEY = 2;
    public static final int TYPE_ACTIVITY = 3;

    private int mType;
    private long mSleepTime;

    public void setType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public void setSleepTime(long sleepTime) {
        mSleepTime = sleepTime;
    }

    public long getSleepTime() {
        return mSleepTime;
    }
}
