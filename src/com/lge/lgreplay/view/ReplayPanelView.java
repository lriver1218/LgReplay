
package com.lge.lgreplay.view;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lge.lgreplay.R;
import com.lge.lgreplay.ReplayService;

public class ReplayPanelView extends LinearLayout {
    private ImageButton mReplayButton;
    private TextView mTouchTextView;

    private boolean mIsReplaying = false;
    private Handler mHandler;

    public ReplayPanelView(Context context, Handler handler) {
        super(context);
        mHandler = handler;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.replay_panel, this, true);

        mReplayButton = (ImageButton) findViewById(R.id.replay_button);
        mReplayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleReplay();
            }
        });
        mTouchTextView = (TextView) findViewById(R.id.touch_text);
    }

    private void toggleReplay() {
        if (!mIsReplaying) {
            mIsReplaying = true;
            mHandler.sendEmptyMessage(ReplayService.MESSAGE_START);
            mReplayButton.setImageResource(R.drawable.stop_replay_button);
        } else {
            setStopImage();
            mHandler.sendEmptyMessage(ReplayService.MESSAGE_STOP);
        }
    }
    
    public void setStopImage() {
        mIsReplaying = false;
        mReplayButton.setImageResource(R.drawable.start_replay_button);
    }
}
