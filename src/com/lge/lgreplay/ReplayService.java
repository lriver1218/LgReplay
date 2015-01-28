
package com.lge.lgreplay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.event.EventKey;
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

    private ReplayPanelView mReplayPanelView = null;
    private WindowManager mWindowManager;

    private com.lge.lgreplay.ReplayThread mReplayThread;

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
            }
        }
    };

    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getBaseContext();

        initViews();
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
        mReplayThread.setLoop(false); // TODO: for test
        mReplayThread.start();
    }

    private void addDummpyList(LinkedList<Event> replayList) {
        // for test
        replayList.add(new EventTouch(252, 1902, EventTouch.ACTION_DOWN));
        replayList.add(new EventTouch(252, 1902, EventTouch.ACTION_UP));
        replayList.add(new EventSleep(1000));
        
        replayList.add(new EventTouch(1248, 1936, EventTouch.ACTION_DOWN));
        replayList.add(new EventTouch(1248, 1936, EventTouch.ACTION_UP));
        replayList.add(new EventSleep(1000));
        
        replayList.add(new EventTouch(252, 1902, EventTouch.ACTION_DOWN));
        replayList.add(new EventTouch(252, 1902, EventTouch.ACTION_UP));
        replayList.add(new EventSleep(1000));
        
        replayList.add(new EventTouch(1261, 2206, EventTouch.ACTION_DOWN));
        replayList.add(new EventTouch(1261, 2206, EventTouch.ACTION_UP));
        replayList.add(new EventSleep(1000));
        
        replayList.add(new EventKey(KeyEvent.KEYCODE_HOME, KeyEvent.ACTION_DOWN, 0));
        replayList.add(new EventKey(KeyEvent.KEYCODE_HOME, KeyEvent.ACTION_UP, 0));
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

        if (mReplayPanelView != null) {
            mWindowManager.removeView(mReplayPanelView);
            mReplayPanelView = null;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
