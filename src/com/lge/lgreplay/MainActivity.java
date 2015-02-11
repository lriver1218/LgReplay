
package com.lge.lgreplay;

import java.io.File;
import java.util.LinkedList;

import com.lge.lgreplay.event.Event;
import com.lge.lgreplay.parser.RepParser;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.util.Log;
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
    private Button mExitButton;

    private boolean mIsBoundReplayService = false;

    private ReplayService mReplayService;

    private LinkedList<Event> mReplayList;

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

    private OnTimeSetListener listener = new OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Toast.makeText(getApplicationContext(), hourOfDay + " hours , " + minute + " mins",
                    Toast.LENGTH_SHORT).show();
        }
    };
}
