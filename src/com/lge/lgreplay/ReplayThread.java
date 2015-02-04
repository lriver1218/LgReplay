
package com.lge.lgreplay;

import java.util.LinkedList;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.event.EventActivity;
import com.lge.lgreplay.event.EventKey;
import com.lge.lgreplay.event.EventOrientation;
import com.lge.lgreplay.event.EventSleep;
import com.lge.lgreplay.event.EventTouch;

public class ReplayThread extends Thread {
    final int ACTION_TOUCH_UI_SHOW = 0;
    final int ACTION_TOUCH_UI_HIDE = 1;
    final int ACTION_TOUCH_UI_UPDATE = 2;
    
    private Context mContext;
    private Handler mHandler = null;
    private LinkedList<Event> mEvents;
    
    private ImageView mTouchView = null;
    WindowManager.LayoutParams params;
    private WindowManager mWindowManager;

    private Instrumentation mInstrumentation;

    private boolean mIsStop = false;
    private boolean mIsLoopReplay = false;
    
    private IActivityManager mActivityManager;
    
    private Handler mUiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.arg1) {
            case ACTION_TOUCH_UI_SHOW:
                mTouchView.setImageAlpha(255);
                break;
                
            case ACTION_TOUCH_UI_HIDE:
                mTouchView.setImageAlpha(0);
                break;
                
            case ACTION_TOUCH_UI_UPDATE:
                mWindowManager.updateViewLayout(mTouchView, params);
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
        initViews();
    }

    public void setLoop(boolean isLoop) {
        mIsLoopReplay = isLoop;
    }

    public void setStop() {
        mIsStop = true;
        mWindowManager.removeView(mTouchView);
    }
    
    private void initViews() {
        mTouchView = new ImageView(mContext);
        mTouchView.setImageResource(R.drawable.ic_replay);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE, // WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.setTitle("LGReplayTouchView");
        sendUiMessage(ACTION_TOUCH_UI_HIDE);

        mWindowManager = (WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE);
        mWindowManager.addView(mTouchView, params);
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
        
        sendFinishMessage();
    }

    private void replayTouch(EventTouch touchEvent) {
        // mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
        // SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, pozx, pozy, 0));

        params.x = (int)touchEvent.getX();
        params.y = (int)touchEvent.getY();
        sendUiMessage(ACTION_TOUCH_UI_UPDATE);
        
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
        params.screenOrientation = orientationEvent.getOrientation();
        sendUiMessage(ACTION_TOUCH_UI_UPDATE);
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
}
