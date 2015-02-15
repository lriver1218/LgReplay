
package com.lge.lgreplay;

import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.event.EventActivity;
import com.lge.lgreplay.event.EventKey;
import com.lge.lgreplay.event.EventOrientation;
import com.lge.lgreplay.event.EventTouch;
import com.lge.lgreplay.view.TouchView;

public class ReplayThread extends Thread {
    private Context mContext;
    private Handler mHandler = null;
    private LinkedList<Event> mEvents;

    private boolean mIsStop = false;
    private boolean mIsLoopReplay = false;
    
    ReplayThread(Context context, Handler handler, LinkedList<Event> events, TouchView touchView) {
        mContext = context;
        mHandler = handler;
        mEvents = events;     
    }

    public void setLoop(boolean isLoop) {
        mIsLoopReplay = isLoop;
    }

    public void setStop() {
        mIsStop = true;
    }
    
    @Override
    public void run() {
        while (!mIsStop) {
        	int numEvents = mEvents.size();
        	for (int i=0; i<numEvents; i++) {
        		Event event = mEvents.get(i);
            	switch (event.getType()) {
	            	case Event.TYPE_TOUCH:
	            		replayTouch((EventTouch) event, i);
	            		//sleepFlag = false;
	            		break;
	            	case Event.TYPE_KEY:
	            		replayKey((EventKey) event);
	            		break;
	            	case Event.TYPE_ACTIVITY:
	            		replayActivity((EventActivity) event);
	            		break;
	            	case Event.TYPE_ORIENTATION:
	            		replayOrientation((EventOrientation) event);
	            		break;
            		default :
            			break;
            	}
                try {
                	if (i < numEvents-1) {
                		sleep(getSleepTime(event, mEvents.get(i+1)));
                	}
				} catch (InterruptedException e) {
				}

                if (mIsStop) {
                    break;
                }
            }

            if (!mIsLoopReplay) {
                break;
            }
        }
        sendMessage(ReplayService.MESSAGE_STOP);
    }

    private void replayTouch(EventTouch touchEvent, int index) {
        if ((index < mEvents.size()-1) && touchEvent.getAction()==MotionEvent.ACTION_DOWN) {
        	if (!ignoreTouchEvent(index)) {
	        	Message message = mHandler.obtainMessage();
	        	message.what = ReplayService.MESSAGE_ACTION_TOUCH_START;
	        	message.arg1 = index;
	        	sendMessage(message);
        	}
        } else if (touchEvent.getAction()==MotionEvent.ACTION_UP) {
        	//sendMessage(ReplayService.MESSAGE_ACTION_TOUCH_STOP);
        }
    }
     
    private void replayKey(EventKey keyEvent) {
        long when = SystemClock.uptimeMillis();
        final KeyEvent event = new KeyEvent(when, when, keyEvent.getAction(), keyEvent.getKey(), keyEvent.getRepeat(),
                0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY | KeyEvent.FLAG_FALLBACK,
                InputDevice.SOURCE_KEYBOARD);
        Log.v("LGReplay", "Key event=" + event);
        InputManager.getInstance().injectInputEvent(event,
                InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }
    
    private void replayActivity(EventActivity activityEvent) {
    	Intent intent = activityEvent.getIntent();
    	Log.v("LGReplay", "Activity intent=" + intent);
    	if (intent != null) {
    		mContext.startActivity(intent);
    	}
    }
    
    private void replayOrientation(EventOrientation orientationEvent) {
    	Message message = mHandler.obtainMessage();
        message.what = ReplayService.MESSAGE_ACTION_SET_ORIENTATION;
        message.arg1 = orientationEvent.getOrientation();
        Log.v("LGReplay", "Orientation event=" + orientationEvent.getOrientation());
        sendMessage(message);
    }

    private boolean ignoreTouchEvent(int index) {
    	if (index < mEvents.size()-2) {
	    	Event event_0 = mEvents.get(index);
	    	Event event_1 = mEvents.get(index+1);
	    	Event event_2 = mEvents.get(index+2);
	    	
	    	if (event_1.getType()==Event.TYPE_TOUCH &&
	    			event_2.getType()==Event.TYPE_ACTIVITY) {
	    		long sleepTimeToUp = getSleepTime(event_0, event_1);
	    		long sleepTimeToActivity = getSleepTime(event_1, event_2);
	    		if (sleepTimeToUp<500 && sleepTimeToActivity<100) {
	    			return true;
	    		}
	    	}
    	}
        
        return false;
    }
    
    private long getSleepTime(Event current, Event next) {
		long currentTime = current.getTime().toMillis();
		long nextTime = next.getTime().toMillis();
		return nextTime-currentTime;
    }

    private void showActivityIsDifferent() {
        Toast.makeText(mContext, "Activity is diffrent of log", Toast.LENGTH_SHORT).show();
    }

    private void sendMessage(int status) {
//    	Message message = mHandler.obtainMessage();
//        message.what = status;
        mHandler.sendEmptyMessage(status);
    }
    
    private void sendMessage(Message message) {
        mHandler.sendMessage(message);
    }
}
