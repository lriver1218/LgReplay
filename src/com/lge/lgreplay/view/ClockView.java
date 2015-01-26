
package com.lge.lgreplay.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClockView extends TextView {
    public ClockView(Context context) {
        super(context);
    }

    public ClockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private static final int UPDATE_INTERVAL = 1000; // 1 second
    private static final int MSG_UPDATE = 1;

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    private final Date resultDate = new Date();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE) {
                invalidate();

                long nextTick = UPDATE_INTERVAL - (System.currentTimeMillis() % 1000);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_UPDATE), nextTick);

                resultDate.setTime(System.currentTimeMillis());
                setText(simpleDateFormat.format(resultDate));
            }
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.sendEmptyMessage(MSG_UPDATE);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(1);
    }
}
