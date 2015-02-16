package com.lge.lgreplay.event;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.lge.lgreplay.TimeInfo;

public class EventActivity extends Event {
	Intent mIntent;
	String mAct;
	String mDat;
	String mCat;
	String mFlg;
	String mCmp;
	String mExtra;

	public EventActivity(Intent intent, TimeInfo time) {
		setType(TYPE_ACTIVITY);
		mIntent = intent;
		setTime(time);
	}

    public EventActivity(String act, String dat, String cat, String flg, String cmp, String extra, TimeInfo time) {
    	mIntent = new Intent();
 		setType(TYPE_ACTIVITY);
 		mAct = act;
 		mDat = dat;
 		mCat = cat;
 		mFlg = flg;
 		mCmp = cmp;
 		mExtra = extra;
 		setTime(time);
 		
 		if (!mAct.isEmpty()) {
 			mIntent.setAction(mAct);
 		}
 		
 		if (!mDat.isEmpty()) {
 			mIntent.setData(Uri.parse(mDat));
 		}
 		
 		if (!mCat.isEmpty()) {
 			mIntent.addCategory(mCat);
 		}
 		if (!mFlg.isEmpty()) {
 			mIntent.addFlags(hexToInt(mFlg, 2, mFlg.length()));
 		} else {
 			mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
 		}
 		if (!mCmp.isEmpty()) {
 			ComponentName comp = getComponent(mCmp);
 			mIntent.setComponent(comp);
 		}
 	}
    
    private int hexToInt (String str, int start, int end) {
    	String flag = str.substring(start, end);
    	return Integer.parseInt(flag, 16);
    }
    
    private ComponentName getComponent(String str) {
    	String[] split = str.split("/");
    	String packageName = split[0];
    	String className = split[0]+split[1];
    	return new ComponentName(packageName, className);
    }

    public boolean hasExtra() {
		if (mExtra.isEmpty()) {
			return false;
		}
		return true;
	}

	public Intent getIntent() {
		return mIntent;
	}
	
	public void setIntent(Intent intent) {
		mIntent = intent;
	}
}
