package com.lge.lgreplay;

import android.text.format.Time;

public class TimeInfo extends Time {
	public int millis;
	
	public TimeInfo() {
		super();
	}
	
	public void set(int millis, int second, int minute, int hour, int monthDay, int month) {
		super.set(second, minute, hour, monthDay, month, 2015);
		this.millis = millis;
	}
	
	public long toMillis(boolean ignoreDst) {
        long res = super.toMillis(ignoreDst);
        res =+ (long) millis;
        return res;
    }
}
