
package com.lge.lgreplay;

import java.io.File;
import java.util.LinkedList;
import java.util.ListIterator;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.parser.RepParser;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ar.com.daidalos.afiledialog.*;

public class MainActivity extends Activity {
	static final boolean debug = false;
	
    private boolean mEnableReplayPanel = false;

    private Button mOpenReplayFileButton;
    private Button mSetStartingTimeButton;
    private Button mToggleReplayPanelButton;
    private Button mExitButton;

    private boolean mIsBoundReplayService = false;

    private ReplayService mReplayService;

    private LinkedList<Event> mReplayList = new LinkedList<Event>();

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mReplayService = ((ReplayService.LocalBinder) service).getService();
            mIsBoundReplayService = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mReplayService = null;
            mIsBoundReplayService = false;
        }
    };

    private RepParser mRepParser;

    void doBindService() {
        if (!mIsBoundReplayService) {
            bindService(new Intent(MainActivity.this, ReplayService.class), mConnection,
                    Context.BIND_AUTO_CREATE);
            Log.d("XXX", "bound");
        }
    }

    void doUnbindService() {
        if (mIsBoundReplayService) {
            unbindService(mConnection);
            mIsBoundReplayService = false;
            Log.d("XXX", "unbound");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initViews();

        mRepParser = new RepParser();
        doBindService();
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

        mExitButton = (Button) findViewById(R.id.exit);
        mExitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsBoundReplayService) {
                    if (mReplayService != null) {
                        mReplayService.stopReplay();
                        mReplayService.hidePanel();
                        mReplayService.stopService(new Intent(MainActivity.this, ReplayService.class));
                        doUnbindService();
                    }
                }
                finish();
            }
        });
    }

    private void openReplayFile() {
        FileChooserDialog dialog = new FileChooserDialog(this);
        dialog.setFilter(".*rep");
        // TODO folder location - temporary
        dialog.loadFolder(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        dialog.addListener(this.onFileSelectedListener);
        dialog.show();
    }

    // rev 1 : dialog
    private void setStartingTime() {
    	if (mReplayList != null && mReplayList.isEmpty()) {
    		Toast.makeText(this, "Please select the REP file first!!" , Toast.LENGTH_LONG).show();
    		return;
    	}
    	TimeInfo startTime = mReplayList.getFirst().getTime();
    	TimeInfo endTime = mReplayList.getLast().getTime();
    	
    	LayoutInflater inflater = (LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.settime_dialog, null);

    	final TextView startText = (TextView)layout.findViewById(R.id.playtime_Start);
    	final TextView endText = (TextView)layout.findViewById(R.id.playtime_End);
    	startText.setText(dateFormat(startTime));
    	endText.setText(dateFormat(endTime));
    	
    	final EditText editMonth = (EditText)layout.findViewById(R.id.edit_month);
    	final EditText editMonthDay = (EditText)layout.findViewById(R.id.edit_monthday);
    	final EditText editHour = (EditText)layout.findViewById(R.id.edit_hour);
    	final EditText editMin = (EditText)layout.findViewById(R.id.edit_min);
    	final EditText editSec = (EditText)layout.findViewById(R.id.edit_sec);
    	final EditText editMil = (EditText)layout.findViewById(R.id.edit_mil);
    	editMonth.setText(dateFormat(startTime.month));
    	editMonthDay.setText(dateFormat(startTime.monthDay));
    	editHour.setText(dateFormat(startTime.hour));
    	editMin.setText(dateFormat(startTime.minute));
    	editSec.setText(dateFormat(startTime.second));
    	editMil.setText(dateFormat(startTime.millis));
    	    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Set Playing Time");
		alert.setView(layout);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// set time
				// toast.. ex) 01-27 10:20:24.463
				Toast.makeText(MainActivity.this, "Edit Time : " 
						+ editMonth.getText() + "-" + editMonthDay.getText() + " " +  editHour.getText() + ":" + editMin.getText() + ":"
						+ editSec.getText() + "." + editMil.getText(), Toast.LENGTH_SHORT).show();
				
				// edit time
				TimeInfo editTime = new TimeInfo();
				editTime.set(Integer.parseInt(editMil.getText().toString()), Integer.parseInt(editSec.getText().toString()),
						Integer.parseInt(editMin.getText().toString()),Integer.parseInt(editHour.getText().toString()),
						Integer.parseInt(editMonthDay.getText().toString()),Integer.parseInt(editMonth.getText().toString()));
				
				// replayList edit
				editReplayList(editTime);
			}
		});
		
		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show(); 
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
        mToggleReplayPanelButton.setText(R.string.disable_replay_panel);
        mReplayService.showPanel();
    }

    private void disableReplayPanel() {
        mEnableReplayPanel = false;
        mToggleReplayPanelButton.setText(R.string.enable_replay_panel);
        mReplayService.hidePanel();
    }

    private FileChooserDialog.OnFileSelectedListener onFileSelectedListener = new FileChooserDialog.OnFileSelectedListener() {
        public void onFileSelected(Dialog source, File file) {
            source.hide();

            Toast.makeText(MainActivity.this, "File selected: " + file.getName(), Toast.LENGTH_SHORT)
                    .show();

            mReplayList = mRepParser.parseFileToList(file);
            mReplayService.setReplayList(mReplayList);
        }

        public void onFileSelected(Dialog source, File folder, String name) {
            source.hide();
            Toast.makeText(MainActivity.this, "File created: " + folder.getName() + "/" + name,
                    Toast.LENGTH_SHORT).show();
        }
    };
    
    public String dateFormat(TimeInfo time) {
    	return String.format("[%02d-%02d %02d:%02d:%02d.%03d]",
    			time.month, time.monthDay, time.hour, time.minute, time.second, time.millis);
    }
    
    public String dateFormat(int time) {
    	return String.format("%02d", time);
    }
    
    private void editReplayList(TimeInfo newTime) {
    	//change linked-list start point
    	LinkedList<Event> tempList = new LinkedList<Event>();
    	ListIterator<Event> listIterator = mReplayList.listIterator();

    	while(listIterator.hasNext()) {
    		Event nextEvent = listIterator.next();
    		if(Time.compare(nextEvent.getTime() , newTime) >= 0) {
    			tempList.add(nextEvent);
    		}
    	}
    	
    	if (debug) {
	    	for(int i = 0; i < tempList.size(); i++) {
	    		System.out.println(dateFormat(tempList.get(i).getTime()));
	    	}
    	}
    	mReplayService.setReplayList(tempList);
    }
}
