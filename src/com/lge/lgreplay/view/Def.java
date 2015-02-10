package com.lge.lgreplay.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

public class Def {
	static Context mContext;
	public static final int SIDEBAR_BOTTOM = 1;
	public static final int SIDEBAR_LEFT = 2;
	public static final int SIDEBAR_RIGHT = 3;
	
	public static final int LEFT = 10;
	public static final int RIGHT = 11;
	
	//public static final int MAIN_WINDOW_TYPE = WindowManager.LayoutParams.TYPE_DISPLAY_OVERLAY;
	public static final int MAIN_WINDOW_TYPE = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
	
	//public static final int BUBBLE_WINDOW_TYPE = WindowManager.LayoutParams.TYPE_NAVIGATION_BAR_PANEL;
	public static final int BUBBLE_WINDOW_TYPE = WindowManager.LayoutParams.TYPE_PHONE;
	
	//public static final int DIALOG_WINDOW_TYPE = WindowManager.LayoutParams.TYPE_DISPLAY_OVERLAY;
	public static final int DIALOG_WINDOW_TYPE = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
	
	public Def() {
	}
	
	public Def(Context context) {
		mContext = context;
	}

	public static int intToDP(Context context, int i)
    {
		mContext = context;
        return (int)TypedValue.applyDimension(1, i, context.getResources().getDisplayMetrics());
    }
	
	public static int screenHeight(Context context)
    {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((WindowManager)context.getSystemService("window")).getDefaultDisplay().getMetrics(displaymetrics);
		if (isTablet(context) && isNavigationBarSupport(context)) {
			return displaymetrics.heightPixels+getNavigationBarHeight(context);
		}
		if (isNavigationBarSupport(context) && getCurrentOrientation(context)==Configuration.ORIENTATION_PORTRAIT &&
				BUBBLE_WINDOW_TYPE != WindowManager.LayoutParams.TYPE_PHONE) {
			return displaymetrics.heightPixels+getNavigationBarHeight(context);
		} else {
			return displaymetrics.heightPixels;
		}
    }
	
	public static int screenWidth(Context context)
    {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((WindowManager)context.getSystemService("window")).getDefaultDisplay().getMetrics(displaymetrics);
		if (isTablet(context)) {
			return displaymetrics.widthPixels;
		}
		if (isNavigationBarSupport(context) && getCurrentOrientation(context)==Configuration.ORIENTATION_LANDSCAPE &&
				BUBBLE_WINDOW_TYPE != WindowManager.LayoutParams.TYPE_PHONE) {
			return displaymetrics.widthPixels+getNavigationBarHeight(context);
		} else {
			return displaymetrics.widthPixels;
		}
    }
	
	public static int getStatusBarHeight(Context context) {
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			return resources.getDimensionPixelSize(resourceId);
		}
		return 0;
	}

	public static int getNavigationBarHeight(Context context){
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0) {
			return resources.getDimensionPixelSize(resourceId);
		}
		return 0;
	}
	
	public static boolean isNavigationBarSupport(Context context){
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("config_showNavigationBar", "bool", "android");
		if (resourceId > 0) {
			return resources.getBoolean(resourceId);
		}
		return false;
	}
	
	public static int getCurrentOrientation(Context context) {
		Resources resources = context.getResources();
		return resources.getConfiguration().orientation;
	}
	
	public static boolean isTablet (Context context) { 
        // TODO: This hacky stuff goes away when we allow users to target devices 
        int xlargeBit = 3; // Configuration.SCREENLAYOUT_SIZE_XLARGE;  // upgrade to HC SDK to get this 
        Configuration config = context.getResources().getConfiguration(); 
        return (config.screenLayout & xlargeBit) == xlargeBit; 
    }
}
