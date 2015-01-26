
package com.lge.lgreplay;

import java.util.LinkedList;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.event.EventActivity;
import com.lge.lgreplay.event.EventKey;
import com.lge.lgreplay.event.EventSleep;
import com.lge.lgreplay.event.EventTouch;

public class ReplayThread extends Thread {
    private Context mContext;
    private Handler mHandler = null;
    private LinkedList<Event> mEvents;

    private Instrumentation mInstrumentation;

    private boolean mIsStop = false;
    private boolean mIsLoopReplay = false;

    ReplayThread(Context context, Handler handler, LinkedList<Event> events) {
        mContext = context;
        mHandler = handler;
        mEvents = events;

        mInstrumentation = new Instrumentation();
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
                }

                if (mIsStop) {
                    break;
                }
            }

            if (!mIsLoopReplay) {
                break;
            }
        }
        
        sendMessage();
    }

    private void replayTouch(EventTouch touchEvent) {
        // mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
        // SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, pozx, pozy, 0));

        mInstrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), touchEvent.getAction(), touchEvent.getX(),
                touchEvent.getY(), 0));
    }

    private void replayKey(EventKey event) {
        // mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_B);
        mInstrumentation.sendKeyDownUpSync(event.getKey());
    }

    private boolean checkActivity(EventActivity event) {
        // TODO
        return true;
    }

    private void showActivityIsDifferent() {
        Toast.makeText(mContext, "Activity is diffrent of log", Toast.LENGTH_SHORT).show();
    }

    private void sendMessage() {
        Message message = mHandler.obtainMessage();
        Bundle data = new Bundle();
        data.putString("state", "finish");
        message.setData(data);
        mHandler.sendMessage(message);
    }
}
