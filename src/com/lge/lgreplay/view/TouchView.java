package com.lge.lgreplay.view;

import com.lge.lgreplay.R;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class TouchView extends FrameLayout {
	Context mContext;
	TextView mKeyName;
	ImageView mTouchSpot;
	int mTouchDiameter;
	WindowManager.LayoutParams params;
	private WindowManager mWindowManager;

	public TouchView(Context context) {
		super(context);
		mContext = context;
        mKeyName = new TextView(mContext);
        mTouchSpot = new ImageView(mContext);
        mTouchDiameter = (int)TypedValue.applyDimension(1, 40, mContext.getResources().getDisplayMetrics());

        mTouchSpot.setImageResource(R.drawable.ic_touch_spot);
        initViews();
	}
	
	private void initViews() {
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
        setKeyVisibility(false);
        setTouchSpotVisibility(false);

        mWindowManager = (WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE);
        mWindowManager.addView(this, params);
        
        mTouchSpot.setImageResource(R.drawable.ic_touch_spot);
        
        addView(mKeyName);
        addView(mTouchSpot);
    }
	
	public void setKeyName(String key) {
		mKeyName.setText(key);
	}
	
	public void setTouchSpot(int x, int y) {
		FrameLayout.LayoutParams touchViewParam = (FrameLayout.LayoutParams)mTouchSpot.getLayoutParams();
		touchViewParam.setMargins(x-mTouchDiameter/2, y-mTouchDiameter/2, 0, 0);
		updateViewLayout(mTouchSpot, touchViewParam);
	}
	
	public void setKeyVisibility(boolean vis) {
		if (vis) {
			mKeyName.setAlpha(255);
		} else {
			mKeyName.setAlpha(0);
		}
	}

	public void setTouchSpotVisibility(boolean vis) {
		if (vis) {
			mTouchSpot.setAlpha(255);
		} else {
			mTouchSpot.setAlpha(0);
		}
	}
	
	public void setOrientation(int orientation) {
		params.screenOrientation = orientation;
		mWindowManager.updateViewLayout(this, params);
	}
	
	public void stopView() {
		mWindowManager.removeView(this);
	}
}
