
package com.lge.lgreplay.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lge.lgreplay.R;
import com.lge.lgreplay.ReplayService;

public class ReplayPanelView extends LinearLayout {
    private static final int REPLAY_PANEL_HIDE_DELAY = 2000;

    private Context mContext;
    private Handler mHandler;
    private boolean mIsReplaying = false;

    private ImageButton mReplayButton;
    private ImageView mReplayBall;

    private Handler mPanelHideHandler = new Handler() {
        public void handleMessage(Message message) {
            if (mIsReplaying) {
                hideReplayButton();
                showReplayBall();
            }
        }
    };

    public ReplayPanelView(Context context, Handler handler) {
        super(context);
        mContext = context;
        mHandler = handler;

        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.replay_panel, this, true);

        mReplayButton = (ImageButton) findViewById(R.id.replay_button);
        mReplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleReplay();
            }
        });

        mReplayBall = (ImageView) findViewById(R.id.replay_ball);
        mReplayBall.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                hideReplayBall();
                showReplayButton();

                mPanelHideHandler.sendEmptyMessageDelayed(0, REPLAY_PANEL_HIDE_DELAY);
                return false;
            }
        });
    }

    public int intToDP(int i) {
        return (int) TypedValue.applyDimension(1, i, mContext.getResources().getDisplayMetrics());
    }

    private void toggleReplay() {
        if (!mIsReplaying) {
            setStart();
            mHandler.sendEmptyMessage(ReplayService.MESSAGE_START);
        } else {
            setStop();
            mHandler.sendEmptyMessage(ReplayService.MESSAGE_STOP);
        }
    }

    private void setStart() {
        mIsReplaying = true;
        mReplayButton.setImageResource(R.drawable.stop_replay_button);

        hideReplayButton();
        showReplayBall();
    }

    public void setStop() {
        mPanelHideHandler.removeMessages(0);

        mIsReplaying = false;
        mReplayButton.setImageResource(R.drawable.start_replay_button);

        hideReplayBall();
        showReplayButton();
    }

    private void showReplayButton() {
        mReplayButton.setVisibility(View.VISIBLE);
    }

    private void hideReplayButton() {
        mReplayButton.setVisibility(View.GONE);
    }

    private void showReplayBall() {
        mReplayBall.setVisibility(View.VISIBLE);
    }

    private void hideReplayBall() {
        mReplayBall.setVisibility(View.GONE);
    }
}
