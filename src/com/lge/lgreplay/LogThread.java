
package com.lge.lgreplay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ComponentName;
import android.os.Handler;
import android.os.Message;

public class LogThread extends Thread {
    private static final String LOGCAT_CLEAR_COMMAND = "logcat -c";
    private static final String LOGCAT_COMMAND = "logcat ActivityManager:*";
    private static final String RESUME_TOP_ACTIVITY_LOCKED = "resumeTopActivityLocked";

    Handler mHandler = null;
    Process mProcess = null;

    boolean mIsStop = false;

    LogThread(Handler handler) {
        mHandler = handler;
    }

    public void setStop() {
        mIsStop = true;
        if (mProcess != null) {
            mProcess.destroy();
        }
    }

    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            Runtime.getRuntime().exec(LOGCAT_CLEAR_COMMAND);
            mProcess = Runtime.getRuntime().exec(LOGCAT_COMMAND);
            reader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));

            while (!mIsStop) {
                String line = reader.readLine();
                if (line != null && line.contains(RESUME_TOP_ACTIVITY_LOCKED)) {
                    parseCurrentActivity(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void parseCurrentActivity(String line) {
        Pattern pattern = Pattern.compile("[\\w.]*/[\\w.]*");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find(10)) {
            String[] results = matcher.group().split("/");
            Message message = mHandler.obtainMessage(ReplayService.MESSAGE_NOW_ACTIVITY);
            message.obj = new ComponentName(results[0], results[1]);
            mHandler.sendMessage(message);
        }
    }
}
