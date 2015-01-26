
package com.lge.lgreplay;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
    private boolean mEnableReplayPanel = false;
    
    private Button mOpenReplayFileButton;
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
        startService(new Intent(this, LGReplayService.class));
        mToggleReplayPanelButton.setText(R.string.disable_replay_panel);
    }

    private void disableReplayPanel() {
        mEnableReplayPanel = false;
        stopService(new Intent(this, LGReplayService.class));
        mToggleReplayPanelButton.setText(R.string.enable_replay_panel);
    }
}
