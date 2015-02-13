package com.lge.lgreplay.event;

import android.content.Intent;

import com.lge.lgreplay.TimeInfo;

public class EventActivity extends Event {
	Intent mIntent;
	
	public EventActivity(Intent intent, TimeInfo time) {
		setType(TYPE_ACTIVITY);
		mIntent = intent;
		setTime(time);
	}
	
	public Intent getIntent() {
		return mIntent;
	}
	
	public void setIntent(Intent intent) {
		mIntent = intent;
	}
}
