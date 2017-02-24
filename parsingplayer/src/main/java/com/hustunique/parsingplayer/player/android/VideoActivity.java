/*
 * Copyright (c) 2017 UniqueStudio
 *
 *
 * This file is part of ParsingPlayer.
 *
 * ParsingPlayer is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ParsingPlayer; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.hustunique.parsingplayer.player.android;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hustunique.parsingplayer.R;
import com.hustunique.parsingplayer.player.view.ParsingVideoView;

/**
 * Created by JianGuo on 2/15/17.
 * Activity supporting playing video in fullscreen
 */
public class VideoActivity extends AppCompatActivity implements View.OnSystemUiVisibilityChangeListener {
    private static final String TAG = "VideoActivity";

    /**
     * the number of milliseconds to wait after user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;

    private ParsingVideoView mVideoView;
    private View mDecorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDecorView = getWindow().getDecorView();
        mDecorView.setOnSystemUiVisibilityChangeListener(this);
        hideSystemUI();
        setContentView(R.layout.activity_video);
        mVideoView = (ParsingVideoView) findViewById(R.id.fullscreen_content);
        mVideoView.setRestoreListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.setTargetTiny();
                finish();
            }
        });
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            Handler handler = new Handler(getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideSystemUI();
                }
            }, AUTO_HIDE_DELAY_MILLIS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.onPause();
    }

    @Override
    public void onBackPressed() {
        mVideoView.setTargetTiny();
        super.onBackPressed();
    }
}
