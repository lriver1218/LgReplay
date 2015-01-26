package com.lge.lgreplay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LogReadingThread extends Thread {
	private static final String TAG = "ReadingThread";

	private static final String UNUSED_LINE = "--";

	Handler mHandler = null;
	String mExec = null;

	Process mProcess = null;

	LogReadingThread(Handler handler, String exec) {
		mHandler = handler;
		mExec = exec;
	}

	public void setStop() {
		if (mProcess != null) {
			mProcess.destroy();
		}
	}

	@Override
	public void run() {
		BufferedReader reader = null;
		try {
			mProcess = Runtime.getRuntime().exec(mExec);
			reader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));

			String line = reader.readLine(); // Ignore the first line, because
												// it is incomplete.
			while (true) {
				line = reader.readLine();

				if (line != null && line.startsWith(UNUSED_LINE) == false) {
					sendMessage(line);
				} else {
					break;
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

	private void sendMessage(String line) {
		Message message = mHandler.obtainMessage();
		Bundle data = new Bundle();
		data.putString("line", line);
		message.setData(data);
		mHandler.sendMessage(message);
	}
}