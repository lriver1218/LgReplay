
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.event.EventKey;
import com.lge.lgreplay.event.EventSleep;
import com.lge.lgreplay.event.EventTouch;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LGReplayService extends Service {
    private static final String TAG = "LGReplayService";

    private View mReplayPanelView = null;
    private WindowManager mWindowManager;

    private boolean mIsRecording = false;
    private ImageButton mReplayButton;

    private com.lge.lgreplay.ReplayThread mReplayThread;

    final Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            String state = (String) message.getData().get("state");
            if("finish".equals(state)) {
                stopReplay();
            }
        }
    };

    private TextView mTouchTextView;

    private Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getBaseContext();

        initViews();
    }

    private void initViews() {
        LayoutInflater li = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mReplayPanelView = (LinearLayout) li.inflate(R.layout.replay_panel, null);
        mReplayButton = (ImageButton) mReplayPanelView.findViewById(R.id.replay_button);
        mReplayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleReplay();
            }
        });
        mTouchTextView = (TextView) mReplayPanelView.findViewById(R.id.touch_text);

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

    private void toggleReplay() {
        if (!mIsRecording) {
            startReplay();
        } else {
            stopReplay();
        }
    }

    private void startReplay() {
        Log.d("XXX", "startReplay");
        mIsRecording = true;
        mReplayButton.setImageResource(R.drawable.stop_replay_button);

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
        
        replayList.add(new EventKey(KeyEvent.KEYCODE_HOME));
    }

    private void stopReplay() {
        Log.d("XXX", "stopReplay");
        mIsRecording = false;

        if (mReplayThread != null) {
            mReplayThread.setStop();
        }

        mReplayButton.setImageResource(R.drawable.start_replay_button);
    }

    // x[0]=773.5, y[0]=1387.0

    private void parseLog(String line) {
        Pattern pattern = Pattern.compile("[xy]\\[\\d\\]=\\d+.\\d+");
        Matcher m = pattern.matcher(line);
        int x = 0;
        int y = 0;

        StringBuilder builder = new StringBuilder();
        if (m.find()) {
            builder.append(m.group(0));
        }

        if (m.find()) {
            builder.append(m.group(0));
        }
        mTouchTextView.setText(builder.toString());
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
