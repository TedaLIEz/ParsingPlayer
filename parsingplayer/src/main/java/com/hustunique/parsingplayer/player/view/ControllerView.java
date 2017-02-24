/*
 *
 * Copyright (c) 2017 UniqueStudio
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

package com.hustunique.parsingplayer.player.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hustunique.parsingplayer.R;
import com.hustunique.parsingplayer.util.LogUtil;
import com.hustunique.parsingplayer.util.Util;

/**
 * Created by JianGuo on 1/20/17.
 * Custom media controller view for video view.
 */
public class ControllerView extends LinearLayout {
    private IMediaPlayerControl mPlayer;
    private static final String TAG = "ControllerView";
    private ImageButton mPauseButton;
    private ImageButton mFullscreenButton;
    private SeekBar mProgress;
    private TextView mCurrentTime, mEndTime;
    private boolean mIsShowing = false;
    private boolean mHasCompleted = false;

    public ControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.media_controller, this);
        initView();
        setVisibility(GONE);
    }

    public ControllerView(Context context) {
        this(context, null);
    }


    // for override in inheritance
    protected void initView() {
        mPauseButton = (ImageButton) findViewById(R.id.pause);
        mProgress = (SeekBar) findViewById(R.id.mediacontroller_progress);
        mEndTime = (TextView) findViewById(R.id.time);
        mCurrentTime = (TextView) findViewById(R.id.time_current);
        mFullscreenButton = (ImageButton) findViewById(R.id.fullscreen);
        mPauseButton.requestFocus();
        mPauseButton.setOnClickListener(mPauseListener);
        mProgress.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setMax(1000);
    }


    private final View.OnClickListener mPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
        }
    };

    private boolean mDragging;

    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            setProgress();
            updatePausePlay();
            if (!mDragging && mIsShowing && mPlayer.isPlaying()) {
                post(mShowProgress);
            }
        }
    };

    public void setRestoreListener(View.OnClickListener listener){
        mFullscreenButton.setOnClickListener(listener);
    }

    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent * 10);
        }

        mEndTime.setText(Util.stringForTime(duration));
        if (!mHasCompleted)
            mCurrentTime.setText(Util.stringForTime(position));
        else if (!mPlayer.isPlaying())
            mCurrentTime.setText(Util.stringForTime(duration));
        else
            mHasCompleted = false;
        return position;
    }

    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newPosition = (duration * progress) / 1000L;
            mPlayer.seekTo((int) newPosition);
            if (mCurrentTime != null)
                mCurrentTime.setText(Util.stringForTime((int) newPosition));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mDragging = true;
            removeCallbacks(mShowProgress);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mDragging = false;
            post(mShowProgress);
        }
    };

    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        post(mShowProgress);
    }

    private void updatePausePlay() {
        // not update image status sometimes
        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_portrait_stop);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_portrait_play);
        }
    }


    public void hide() {
        if (mIsShowing) {
            mIsShowing = false;
            removeCallbacks(mShowProgress);
            setVisibility(GONE);
        }
    }

    public void complete() {
        mHasCompleted = true;
        removeCallbacks(mShowProgress);
        setProgress();
        updatePausePlay();
    }


    public void setMediaPlayer(IMediaPlayerControl player) {
        mPlayer = player;
    }


    public void show() {
        LogUtil.d(TAG,"controller shows");
        if (!mIsShowing) {
            setVisibility(VISIBLE);
            mIsShowing = true;
            post(mShowProgress);
            mPauseButton.requestFocus();
        }
    }


    public boolean isShowing() {
        return mIsShowing;
    }


}
