
package com.lge.lgreplay;

import java.io.File;
import java.util.LinkedList;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.event.EventKey;
import com.lge.lgreplay.event.EventOrientation;
import com.lge.lgreplay.event.EventSleep;
import com.lge.lgreplay.event.EventTouch;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.*;


public class MainActivity extends Activity {
    private boolean mEnableReplayPanel = false;
    
    private Button mOpenReplayFileButton;
    private Button mSetStartingTimeButton;
    private Button mToggleReplayPanelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        mOpenReplayFileButton = (Button) findViewById(R.id.open_replay_file);
        mOpenReplayFileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openReplayFile();
            }

        });

        mSetStartingTimeButton = (Button) findViewById(R.id.set_starting_time);
        mSetStartingTimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setStartingTime();
            }
        });

        mToggleReplayPanelButton = (Button) findViewById(R.id.toggle_replay_panel);
        mToggleReplayPanelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleReplayPanel();
            }
        });
    }

    private void openReplayFile() {
        // TODO : Open File Dialog
        /*
        <6>[ 7652.492628 / 01-05 05:36:41.481] lge_touch 2-004a: 1 FINGER Pressed <0> : x[ 817] y[2902], z[ 40]
        <6>[ 7652.557238 / 01-05 05:36:41.541] lge_touch 2-004a: FINGER Released <0> <0 P>
        */
		// Create the dialog.
		FileChooserDialog dialog = new FileChooserDialog(this);
		
		dialog.setFilter(".*rep|.*log|.*log.*");
		// folder location - temporary
		dialog.loadFolder(Environment.getExternalStorageDirectory() + "/Download/");
		dialog.setShowConfirmation(true, false);
		// Assign listener for the select event.
		dialog.addListener(this.onFileSelectedListener);
		dialog.show();
    }

    // rev 1 : dialog  
    private void setStartingTime() {
    	TimePickerDialog dialog = new TimePickerDialog(this, listener, 15, 24, false);
    	dialog.show();
    }
    
    private void toggleReplayPanel() {
        if (!mEnableReplayPanel) {
            enableReplayPanel();
        } else {
            disableReplayPanel();
        }
    }

    private void enableReplayPanel() {
        mEnableReplayPanel = true;
        startService(new Intent(this, ReplayService.class));
        mToggleReplayPanelButton.setText(R.string.disable_replay_panel);
    }

    private void disableReplayPanel() {
        mEnableReplayPanel = false;
        stopService(new Intent(this, ReplayService.class));
        mToggleReplayPanelButton.setText(R.string.enable_replay_panel);
    }
 
 
	// ---- Methods for display the results ----- //
	private FileChooserDialog.OnFileSelectedListener onFileSelectedListener = new FileChooserDialog.OnFileSelectedListener() {
		public void onFileSelected(Dialog source, File file) {
			source.hide();
			Toast toast = Toast.makeText(MainActivity.this, "File selected: " + file.getName(), Toast.LENGTH_LONG);
			toast.show();
			
			// add linked list
			// Do Something  ...
	        LinkedList<Event> replayList = new LinkedList<Event>();
	        addDummpyList(replayList); // for test
	        ReplayService.setReplayList(replayList);
		}
		public void onFileSelected(Dialog source, File folder, String name) {
			source.hide();
			Toast toast = Toast.makeText(MainActivity.this, "File created: " + folder.getName() + "/" + name, Toast.LENGTH_LONG);
			toast.show();
		}
	};
	
	private OnTimeSetListener listener = new OnTimeSetListener() {		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Toast.makeText(getApplicationContext(), hourOfDay + " hours , " + minute + " mins", Toast.LENGTH_SHORT).show();
		}
	};
	
	// test..
    private void addDummpyList(LinkedList<Event> replayList) {
        // for test
        TimeInfo time = new TimeInfo();
        time.set(274, 35, 12, 14, 26, 1);
        replayList.add(new EventTouch(985, 1330, EventTouch.ACTION_DOWN, time));
        replayList.add(new EventSleep(250));
        replayList.add(new EventTouch(998, 1325, EventTouch.ACTION_UP, time));
        replayList.add(new EventSleep(2000));

        replayList.add(new EventTouch(633, 1129, EventTouch.ACTION_DOWN, time));
        replayList.add(new EventSleep(200));
        replayList.add(new EventTouch(633, 1129, EventTouch.ACTION_UP, time));
        replayList.add(new EventSleep(1000));

        replayList.add(new EventTouch(580, 861, EventTouch.ACTION_DOWN, time));
        replayList.add(new EventSleep(100));
        replayList.add(new EventTouch(580, 861, EventTouch.ACTION_UP, time));
        replayList.add(new EventSleep(2000));

        replayList.add(new EventOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, time));
        replayList.add(new EventSleep(3000));
        replayList.add(new EventOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, time));
        replayList.add(new EventSleep(2000));

        replayList.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_DOWN, 0, time));
        replayList.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_UP, 0, time));
        replayList.add(new EventSleep(1000));
        replayList.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_DOWN, 0, time));
        replayList.add(new EventKey(KeyEvent.KEYCODE_POWER, KeyEvent.ACTION_UP, 0, time));
    }
}
