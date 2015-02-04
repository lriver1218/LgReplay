
package com.lge.lgreplay;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.event.EventKey;
import com.lge.lgreplay.event.EventOrientation;
import com.lge.lgreplay.event.EventSleep;
import com.lge.lgreplay.event.EventTouch;
import com.lge.lgreplay.view.ReplayPanelView;

import java.util.LinkedList;

public class ReplayService extends Service {
    private static final String TAG = "LGReplayService";

    public static final int MESSAGE_START = 0;
    public static final int MESSAGE_STOP = 1;
    public static final int MESSAGE_PAUSE = 2;
    public static final int MESSAGE_RESUME = 3;
    public static final int MESSAGE_SPEED = 4;
    public static final int MESSAGE_FINISH = 5;
    public static final int MESSAGE_NOW_ACTIVITY = 6;

    private ReplayPanelView mReplayPanelView = null;
    private WindowManager mWindowManager;

    private Context mContext;

    private LogThread mLogThread;
    private ReplayThread mReplayThread;

    private ComponentName mCurrentActivity;

    final Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == MESSAGE_START) {
                startReplay();
            } else if (message.what == MESSAGE_STOP) {
                stopReplay();
            } else if (message.what == MESSAGE_PAUSE) {

            } else if (message.what == MESSAGE_RESUME) {

            } else if (message.what == MESSAGE_SPEED) {

            } else if (message.what == MESSAGE_FINISH) {
                stopReplay();
            } else if (message.what == MESSAGE_NOW_ACTIVITY) {
                setCurrentActivity((ComponentName) message.obj);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getBaseContext();

        initViews();

        mLogThread = new LogThread(mHandler);
        mLogThread.start();
    }

    private void initViews() {
        mReplayPanelView = new ReplayPanelView(mContext, mHandler);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE, // WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.y = 100; // TODO : status bar height
        params.setTitle("LGReplayPanel");

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mReplayPanelView, params);
    }

    private void startReplay() {
        LinkedList<Event> replayList = new LinkedList<Event>();

        addDummpyList(replayList); // for test

        mReplayThread = new ReplayThread(mContext, mHandler, replayList);
        // mReplayThread.setLoop(true); // TODO: for test
        mReplayThread.start();
    }

    private void addDummpyList(LinkedList<Event> replayList) {
        // for test
        TimeInfo time = new TimeInfo();
        time.set(274, 35, 12, 14, 26, 1);
        replayList.add(new EventTouch(985, 1330, EventTouch.ACTION_DOWN, time));
        replayList.add(new EventSleep(250));
        replayList.add(new EventTouch(998, 1325, EventTouch.ACTION_UP, time));
        replayList.add(new EventSleep(2000));

        replayList.add(new EventTouch(633, 1129, EventTouch.ACTION_DOWN, time));
        replayList.add(new EventSleep(200));
        replayList.add(new EventTouch(633, 1129, EventTouch.ACTION_UP, time));
        replayList.add(new EventSleep(1000));

        replayList.add(new EventTouch(580, 861, EventTouch.ACTION_DOWN, time));
        replayList.add(new EventSleep(100));
        replayList.add(new EventTouch(580, 861, EventTouch.ACTION_UP, time));
        replayList.add(new EventSleep(2000));

        replayList.add(new EventOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, time));
        replayList.add(new EventSleep(3000));
        replayList.add(new EventOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, time));
        replayList.add(new EventSleep(2000));

        replayList.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_DOWN, 0, time));
        replayList.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_UP, 0, time));
        replayList.add(new EventSleep(1000));
        replayList.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_DOWN, 0, time));
        replayList.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_UP, 0, time));
    }

    private void stopReplay() {
        mReplayPanelView.setStopImage();
        if (mReplayThread != null) {
            mReplayThread.setStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLogThread.setStop();
        if (mReplayPanelView != null) {
            mWindowManager.removeView(mReplayPanelView);
            mReplayPanelView = null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void setCurrentActivity(ComponentName currentActivity) {
        mCurrentActivity = currentActivity;
    }

    public ComponentName getCurrentActivity() {
        return mCurrentActivity;
    }
}
