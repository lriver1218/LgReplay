
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.event.EventKey;
import com.lge.lgreplay.event.EventOrientation;
import com.lge.lgreplay.event.EventSleep;
import com.lge.lgreplay.event.EventTouch;
import com.lge.lgreplay.view.Def;
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
    private static LinkedList<Event> replayList = null;
    
    
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
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        initViews();

        mLogThread = new LogThread(mHandler);
        mLogThread.start();
    }

    private void initViews() {
        mReplayPanelView = new ReplayPanelView(mContext, mHandler);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_NAVIGATION_BAR_PANEL,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.setTitle("LGReplayPanel");

        mWindowManager.addView(mReplayPanelView, params);
    }
    
    public int intToDP(int i) {
        return (int)TypedValue.applyDimension(1, i, mContext.getResources().getDisplayMetrics());
    }

    private void startReplay() {
        mReplayThread = new ReplayThread(mContext, mHandler, replayList);
        // mReplayThread.setLoop(true); // TODO: for test
        mReplayThread.start();
    }

    private void stopReplay() {
        mReplayPanelView.setStop();
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
    
    public static void setReplayList(LinkedList<Event> rpList) {
    	replayList = rpList;
    }
}
