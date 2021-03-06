package com.lge.lgreplay;

import java.util.LinkedList;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.event.EventTouch;
import com.lge.lgreplay.view.TouchView;

import android.app.Instrumentation;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;

public class TouchEventThread extends Thread {
	Context mContext;
	Handler mHandler;
	LinkedList<Event> mEvents;
	Instrumentation mInstrumentation;
	
	boolean mIsStop = false;
	
	boolean mTouchEventStop = false;
	
	EventTouch current;
	EventTouch next;
	
	TouchView mTouchView;
	
	final Object mLock = new Object();
	
	TouchEventThread(Context context, Handler handler, LinkedList<Event> events, TouchView touchView) {
		mContext = context;
        mHandler = handler;
        mEvents = events;
        mTouchView = touchView;
        //mMsg = handler.obtainMessage();
	}
	
	public void setStop() {
		mIsStop = true;
	}
	
	public void startTouchEvent(int index) {
		current = (EventTouch) mEvents.get(index);
		next = (EventTouch) mEvents.get(index+1);

		if (next.getAction()==MotionEvent.ACTION_UP) {
			mTouchEventStop = true;
		}
    }
	
	public void stopTouchEvent() {
		//mMoveEventStop = false;
		//sendPointerEvent(MotionEvent.ACTION_UP, next.getX(), next.getY());
	}

	@Override
	public void run() {
		while(!mIsStop) {
			if (mTouchEventStop) {
				long sleepTime = next.getTime().toMillis()-current.getTime().toMillis();
				int cycle = (int)sleepTime/20;
				float parent = Math.abs(current.getX()-next.getX());
				float child = Math.abs(current.getY()-next.getY());
				float gradient = 0;
				if (parent != 0 || child != 0) {
					gradient = child / parent;
				}
				
				Log.v("LGReplay", "Touch X=["+current.getX()+"->"+next.getX()+"], Y=["+current.getY()+"->"+next.getY()+
						"], Time=" + sleepTime + ", Cycle=" + cycle + ", Gradient=" + gradient);
				
				sendMessage(ReplayService.MESSAGE_ACTION_SET_TOUCH_SPOT, (int)current.getX(), (int)current.getY());
				sendMessage(ReplayService.MESSAGE_ACTION_TOUCH_UI_SHOW);
				sendPointerEvent(MotionEvent.ACTION_DOWN, current.getX(), current.getY());
				for (int i=0; i<cycle; i++) {
					sendMoveEvent(cycle, gradient, i);
		    	}
				//sendMessage(ReplayService.MESSAGE_ACTION_SET_TOUCH_SPOT, (int)next.getX(), (int)next.getY());
				sendMessage(ReplayService.MESSAGE_ACTION_TOUCH_UI_HIDE);
				sendPointerEvent(MotionEvent.ACTION_UP, next.getX(), next.getY());
				
				mTouchEventStop = false;
			}
			try {
				sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				Log.v("LGReplay", "Touch Interrupted");
			}
		}
	}
	
	private void sendMoveEvent(int cycle, float gradient, int index) {
		if (mTouchEventStop) {
			float distance = Math.abs(next.getX()-current.getX()) / cycle;
			float x = 0;
			float y = 0;
			if (next.getX()<current.getX()) {
				x = current.getX()-distance*(index+1);
				y = gradient*(current.getX()-x) + current.getY();
				sendPointerEvent(MotionEvent.ACTION_MOVE, x, y);
			} else if (next.getX()>current.getX()) {
				x = current.getX()+distance*(index+1);
				y = gradient*(next.getX()-x) + next.getY();
				sendPointerEvent(MotionEvent.ACTION_MOVE, x, y);
			} else {
				// do nothing
			}
		}
	}
	
	private void sendPointerEvent(int action, float x, float y) {
    	MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(),
    			SystemClock.uptimeMillis(), action, x, y, 0);
    	if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) == 0) {
    		event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
    	}
    	InputManager.getInstance().injectInputEvent(event,
    			InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
    }
	
    private void sendMessage(int status) {
        mHandler.sendEmptyMessage(status);
    }
    
    private void sendMessage(int status, int x, int y) {
    	Message message = mHandler.obtainMessage();
		message.what = status;
		message.arg1 = x;
		message.arg2 = y;
        mHandler.sendMessage(message);
    }
}
