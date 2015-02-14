package com.lge.lgreplay.event;

import android.content.Intent;

import com.lge.lgreplay.TimeInfo;

public class EventActivity extends Event {
	Intent mIntent;
	String mAct;
	String mCat;
	String mFlg;
	String mCmp;

	public EventActivity(Intent intent, TimeInfo time) {
		setType(TYPE_ACTIVITY);
		mIntent = intent;
		setTime(time);
	}

    public EventActivity(String act, String cat, String flg, String cmp,TimeInfo time) {
 		setType(TYPE_ACTIVITY);
 		mAct = act;
 		mCat = cat;
 		mFlg = flg;
 		mCmp = cmp;
 		setTime(time);
 	}

	public Intent getIntent() {
		return mIntent;
	}
	
	public void setIntent(Intent intent) {
		mIntent = intent;
	}
}
