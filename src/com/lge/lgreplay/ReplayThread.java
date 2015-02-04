
package com.lge.lgreplay;

import java.util.LinkedList;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Bundle;
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
import com.lge.lgreplay.event.EventSleep;
import com.lge.lgreplay.event.EventTouch;
import com.lge.lgreplay.view.TouchView;

public class ReplayThread extends Thread {
    final int ACTION_TOUCH_UI_SHOW = 0;
    final int ACTION_TOUCH_UI_HIDE = 1;
    final int ACTION_KEY_UI_SHOW = 3;
    final int ACTION_KEY_UI_HIDE = 4;
    final int ACTION_SET_TOUCH_SPOT = 5;
    final int ACTION_SET_KEY_NAME = 6;
    final int ACTION_SET_ORIENTATION = 7;
    final int ACTION_EXIT = 10;
    
    private Context mContext;
    private Handler mHandler = null;
    private LinkedList<Event> mEvents;
    
    private TouchView mTouchView = null;

    private Instrumentation mInstrumentation;

    private boolean mIsStop = false;
    private boolean mIsLoopReplay = false;
    
    private IActivityManager mActivityManager;
    
    private Handler mUiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.arg1) {
            case ACTION_TOUCH_UI_SHOW:
                mTouchView.setTouchSpotVisibility(true);
                break;
                
            case ACTION_TOUCH_UI_HIDE:
            	mTouchView.setTouchSpotVisibility(false);
                break;
                
            case ACTION_KEY_UI_SHOW:
                mTouchView.setKeyVisibility(true);
                break;
                
            case ACTION_KEY_UI_HIDE:
                mTouchView.setKeyVisibility(false);
                break;
                
            case ACTION_SET_TOUCH_SPOT:
                mTouchView.setTouchSpot(msg.getData().getInt("x"), msg.getData().getInt("y"));
                break;
                
            case ACTION_SET_KEY_NAME:
                // Todo
                break;
                
            case ACTION_SET_ORIENTATION:
                mTouchView.setOrientation(msg.getData().getInt("orientation"));
                break;
                
            case ACTION_EXIT:
                mTouchView.stopView();
                break;
                
            default:
                break;
            }
        }
    };

    ReplayThread(Context context, Handler handler, LinkedList<Event> events) {
        mContext = context;
        mHandler = handler;
        mEvents = events;

        mInstrumentation = new Instrumentation();
        mActivityManager = ActivityManagerNative.getDefault();
        mTouchView = new TouchView(context);
    }

    public void setLoop(boolean isLoop) {
        mIsLoopReplay = isLoop;
    }

    public void setStop() {
        mIsStop = true;
    }
    
    @Override
    public void run() {
        Log.d("XXX", "run");

        /*
        <3>[   85.668952 / 01-01 10:56:18.949][3] [Touch] 1 finger pressed: <0> x[ 848] y[1450] z[ 58]
        <3>[   85.668982 / 01-01 10:56:18.949][3] [Touch] 2 finger pressed: <1> x[ 996] y[2454] z[ 34]
        <3>[   85.691667 / 01-01 10:56:18.979][0] [Touch] touch_release[ ]: <1> x[ 996] y[2454]
        <3>[   85.766127 / 01-01 10:56:19.049][0] [Touch] touch_release[ ]: <0> x[1249] y[1426]
        */

        while (!mIsStop) {
            for (Event event : mEvents) {
                if (event.getType() == Event.TYPE_SLEEP) {
                    try {
                        sleep(((EventSleep)event).getSleepTime());
                    } catch (InterruptedException e) {
                    }
                } else if (event.getType() == Event.TYPE_TOUCH) {
                    replayTouch((EventTouch) event);
                } else if (event.getType() == Event.TYPE_KEY) {
                    replayKey((EventKey) event);
                } else if (event.getType() == Event.TYPE_ACTIVITY) {
                    if (!checkActivity((EventActivity) event)) {
                        showActivityIsDifferent();
                    }
                } else if (event.getType() == Event.TYPE_ORIENTATION) {
                    replayOrientation((EventOrientation) event);
                }

                if (mIsStop) {
                    break;
                }
            }

            if (!mIsLoopReplay) {
                break;
            }
        }
        sendUiMessage(ACTION_EXIT);
        sendFinishMessage();
    }

    private void replayTouch(EventTouch touchEvent) {
        // mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
        // SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, pozx, pozy, 0));

        Bundle data = new Bundle();
        data.putInt("x", (int)touchEvent.getX());
        data.putInt("y", (int)touchEvent.getY());
        sendUiMessageWithData(ACTION_SET_TOUCH_SPOT, data);
        
        if (touchEvent.getAction()==MotionEvent.ACTION_DOWN) {
            sendUiMessage(ACTION_TOUCH_UI_SHOW);
        } else {
            sendUiMessage(ACTION_TOUCH_UI_HIDE);
        }
        mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), touchEvent.getAction(), touchEvent.getX(),
                touchEvent.getY(), 0));
        
//      MotionEvent event = MotionEvent.obtain(SystemClock.uptimeMillis(),
//              SystemClock.uptimeMillis(), touchEvent.getAction(), touchEvent.getX(),
//              touchEvent.getY(), 0);
//      if ((event.getSource() & InputDevice.SOURCE_CLASS_POINTER) == 0) {
//            event.setSource(InputDevice.SOURCE_TOUCHSCREEN);
//        }
//      InputManager.getInstance().injectInputEvent(event,
//              InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
    }

    private void replayKey(EventKey keyEvent) {
        // mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_B);
        //mInstrumentation.sendKeyDownUpSync(event.getKey());

        long when = SystemClock.uptimeMillis();
        final KeyEvent event = new KeyEvent(when, when, keyEvent.getAction(), keyEvent.getKey(), keyEvent.getRepeat(),
                0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY | KeyEvent.FLAG_FALLBACK,
                InputDevice.SOURCE_KEYBOARD);
        InputManager.getInstance().injectInputEvent(event,
                InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }
    
    private void replayOrientation(EventOrientation orientationEvent) {
        Bundle data = new Bundle();
        data.putInt("orientation", orientationEvent.getOrientation());
        sendUiMessageWithData(ACTION_SET_ORIENTATION, data);
    }

    private boolean checkActivity(EventActivity event) {
        // TODO
        return true;
    }

    private void showActivityIsDifferent() {
        Toast.makeText(mContext, "Activity is diffrent of log", Toast.LENGTH_SHORT).show();
    }

    private void sendFinishMessage() {
        mHandler.sendEmptyMessage(ReplayService.MESSAGE_FINISH);
    }
    
    private void sendUiMessage(int status) {
        Message message = mUiHandler.obtainMessage();
        message.arg1 = status;
        mUiHandler.sendMessage(message);
    }
    
    private void sendUiMessageWithData(int status, Bundle data) {
        Message message = mUiHandler.obtainMessage();
        message.arg1 = status;
        message.setData(data);
        mUiHandler.sendMessage(message);
    }
}
