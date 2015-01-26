
package com.lge.lgreplay.event;

public class EventKey extends Event {
    int mKey;

    public EventKey(int key) {
        setType(TYPE_KEY);

        mKey = key;
    }

    public int getKey() {
        return mKey;
    }

    public void setKey(int key) {
        mKey = key;
    }
}
