
package com.lge.lgreplay.event;

public class EventSleep extends Event {
    private long mSleepTime;

    public EventSleep(long sleepTime) {
        setSleepTime(sleepTime);
    }

    public void setSleepTime(long sleepTime) {
        mSleepTime = sleepTime;
    }

    public long getSleepTime() {
        return mSleepTime;
    }
}
