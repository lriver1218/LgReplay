
package com.lge.lgreplay;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.view.ReplayPanelView;
import com.lge.lgreplay.view.TouchView;

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
    
    // for TouchEventThread
    public static final int MESSAGE_ACTION_TOUCH_START = 100;
    public static final int MESSAGE_ACTION_TOUCH_STOP = 101;
    
    // for UI Handling
    public static final int MESSAGE_ACTION_TOUCH_UI_SHOW = 200;
    public static final int MESSAGE_ACTION_TOUCH_UI_HIDE = 201;
    public static final int MESSAGE_ACTION_KEY_UI_SHOW = 202;
    public static final int MESSAGE_ACTION_KEY_UI_HIDE = 203;
    public static final int MESSAGE_ACTION_SET_TOUCH_SPOT = 204;
    public static final int MESSAGE_ACTION_SET_KEY_NAME = 205;
    public static final int MESSAGE_ACTION_SET_ORIENTATION = 206;
    public static final int MESSAGE_ACTION_TOUCH_VIEW_EXIT = 207;

    private ReplayPanelView mReplayPanelView = null;
    private WindowManager mWindowManager;

    private Context mContext;

    private LogThread mLogThread;
    private ReplayThread mReplayThread;
    private TouchEventThread mTouchEventThread;

    private ComponentName mCurrentActivity;
    private LinkedList<Event> mReplayList = null;
	private TouchView mTouchView;

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
            } else if (message.what == MESSAGE_ACTION_TOUCH_START) {
            	mTouchEventThread.interrupt();
            	mTouchEventThread.startTouchEvent(message.arg1);
            } else if (message.what == MESSAGE_ACTION_TOUCH_STOP) {
            	mTouchEventThread.stopTouchEvent();
            } else if (message.what == MESSAGE_ACTION_TOUCH_UI_SHOW) {
            	mTouchView.setTouchSpotVisibility(true);
            } else if (message.what == MESSAGE_ACTION_TOUCH_UI_HIDE) {
            	if (mTouchView != null) {
            		mTouchView.setTouchSpotVisibility(false);
            	}
            } else if (message.what == MESSAGE_ACTION_KEY_UI_SHOW) {
            	mTouchView.setKeyVisibility(true);
            } else if (message.what == MESSAGE_ACTION_KEY_UI_HIDE) {
            	mTouchView.setKeyVisibility(false);
            } else if (message.what == MESSAGE_ACTION_SET_TOUCH_SPOT) {
            	mTouchView.setTouchSpot(message.arg1, message.arg2);
            } else if (message.what == MESSAGE_ACTION_SET_KEY_NAME) {
            	// Todo
            } else if (message.what == MESSAGE_ACTION_SET_ORIENTATION) {
            	mTouchView.setOrientation(message.arg1);
            } else if (message.what == MESSAGE_ACTION_TOUCH_VIEW_EXIT) {
            	mTouchView.stopView();
            }
        }
    };

    public class LocalBinder extends Binder {
        ReplayService getService() {
            return ReplayService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getBaseContext();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
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
        return (int) TypedValue.applyDimension(1, i, mContext.getResources().getDisplayMetrics());
    }

    private void startReplay() {
        if (mReplayList != null && mReplayList.size() > 0) {
			mTouchView = new TouchView(mContext);
            mReplayThread = new ReplayThread(mContext, mHandler, mReplayList, mTouchView);
            // mReplayThread.setLoop(true); // TODO: for test
            mReplayThread.start();
            mTouchEventThread = new TouchEventThread(mContext, mHandler, mReplayList, mTouchView);
            mTouchEventThread.start();
        } else {
            Toast.makeText(mContext, R.string.no_replay_list, Toast.LENGTH_SHORT).show();
            mReplayPanelView.setStop();
        }
    }

    public void stopReplay() {
        if (mReplayPanelView != null) {
            mReplayPanelView.setStop();
        }
        
        if (mReplayThread != null) {
            mReplayThread.setStop();
        }
		if (mTouchEventThread != null) {
			mTouchEventThread.setStop();
        }
		if (mTouchView != null) {
			mTouchView.stopView();
			mTouchView = null;
		}
    }

    public void setCurrentActivity(ComponentName currentActivity) {
        mCurrentActivity = currentActivity;
    }

    public ComponentName getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setReplayList(LinkedList<Event> list) {
        mReplayList = list;
    }

    public void showPanel() {
        initViews();

        mLogThread = new LogThread(mHandler);
        mLogThread.start();
    }

    public void hidePanel() {
        if (mLogThread != null) {
            mLogThread.setStop();
        }

        if (mReplayPanelView != null) {
            mWindowManager.removeView(mReplayPanelView);
            mReplayPanelView = null;
        }
    }
}
