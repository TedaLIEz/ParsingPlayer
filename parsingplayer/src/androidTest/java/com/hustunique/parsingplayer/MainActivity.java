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

package com.hustunique.parsingplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hustunique.parsingplayer.player.view.ParsingVideoView;

public class MainActivity extends AppCompatActivity {
    private ParsingVideoView mVideoView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = (ParsingVideoView) findViewById(R.id.videoView);
    }

    // turn black when resume to this activity
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
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.onDestroy();
    }
}
