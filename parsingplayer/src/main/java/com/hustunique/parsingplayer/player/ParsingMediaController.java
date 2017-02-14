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

package com.hustunique.parsingplayer.player;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hustunique.parsingplayer.R;
import com.orhanobut.logger.Logger;

import java.util.Formatter;
import java.util.Locale;

/**
 * Created by JianGuo on 1/20/17.
 * Custom media controller view for video view.
 */
public class ParsingMediaController implements IMediaController {
    private IMediaPlayerControl mPlayer;
    private static final int sDefaultTimeOut = 5000;
    private static final String TAG = "ParsingMediaController";
    private View mRoot;
    private Context mContext;
    private View mAnchor;
    private ImageButton mPauseButton, mQualityButton;
    private SeekBar mProgress;
    private TextView mCurrentTime, mEndTime;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mParams;
    private boolean mIsShowing = false;
    private boolean mHasCompleted = false;

    ParsingMediaController(Context context, AttributeSet attrs) {
        mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mRoot = initControllerView();
        initPopupWindow();
    }

    ParsingMediaController(Context context) {
        this(context, null);
    }


    private void initPopupWindow() {
        mParams = createLayoutParams(mRoot.getWindowToken());
    }

    private WindowManager.LayoutParams createLayoutParams(IBinder windowToken) {
        final WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.token = windowToken;
        p.format = PixelFormat.TRANSLUCENT;
        p.gravity = Gravity.START | Gravity.TOP;
        p.packageName = mContext.getPackageName();
        p.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        return p;
    }

    // for override in inheritance
    protected View initControllerView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRoot = inflater.inflate(R.layout.media_controller, new FrameLayout(mContext), false);
        mPauseButton = (ImageButton) mRoot.findViewById(R.id.pause);
        if (mPauseButton != null) {
            mPauseButton.requestFocus();
            mPauseButton.setOnClickListener(mPauseListener);
        }
        mQualityButton = (ImageButton) mRoot.findViewById(R.id.quality);
        if (mQualityButton != null) {
            mQualityButton.setOnClickListener(mQualityListener);
        }
        mProgress = (SeekBar) mRoot.findViewById(R.id.mediacontroller_progress);
        if (mProgress != null) {
            mProgress.setOnSeekBarChangeListener(mSeekListener);
            mProgress.setMax(1000);
        }
        mEndTime = (TextView) mRoot.findViewById(R.id.time);
        mCurrentTime = (TextView) mRoot.findViewById(R.id.time_current);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        show();
                        break;
                    case MotionEvent.ACTION_UP:
                        show();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        hide();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        return mRoot;
    }


    private final View.OnClickListener mQualityListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPlayer != null && isShowing()) {
                if (mPlayer.isQualityViewShown())
                    mPlayer.hideQualityView();
                else
                    mPlayer.showQualityView();
            }
        }
    };

    private final View.OnClickListener mPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
            show();
        }
    };

    private boolean mDragging;

    private final Runnable mShowProgress = new Runnable() {
        @Override
        public void run() {
            int pos = setProgress();
            if (!mDragging && mIsShowing && mPlayer.isPlaying()) {
                mRoot.postDelayed(mShowProgress, 1000 - (pos % 1000));
            }
        }
    };


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

        if (mEndTime != null)
            mEndTime.setText(stringForTime(duration));
        if (mCurrentTime != null)
            mCurrentTime.setText(stringForTime(position));
        if (mHasCompleted)
            mCurrentTime.setText(stringForTime(duration));
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
            long newposition = (duration * progress) / 1000L;
            mPlayer.seekTo((int) newposition);
            if (mCurrentTime != null)
                mCurrentTime.setText(stringForTime((int) newposition));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mDragging = true;
            mRoot.removeCallbacks(mShowProgress);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mDragging = false;
            mRoot.post(mShowProgress);
        }
    };

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }


    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay();
    }

    private void updatePausePlay() {
        if (mRoot == null || mPauseButton == null)
            return;
        if (mPlayer.isPlaying()) {
            mPauseButton.setImageResource(R.drawable.ic_pause_white_24dp);
        } else {
            mPauseButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }


    @Override
    public void hide() {
        if (mAnchor == null)
            return;

        if (mIsShowing) {
            if (mRoot.getParent() != null) {
                mWindowManager.removeViewImmediate(mRoot);
                ViewParent contentParent = mRoot.getParent();
                if (contentParent instanceof ViewGroup) {
                    ((ViewGroup) contentParent).removeView(mRoot);
                }
            }
            mIsShowing = false;
            mRoot.removeCallbacks(mShowProgress);
        }
    }

    @Override
    public void complete() {
        mHasCompleted = true;
        mRoot.removeCallbacks(mShowProgress);
        updatePausePlay();
    }


    private View.OnLayoutChangeListener mOnLayoutListener = new View.OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            updateAnchorViewLayout();
            if (isShowing()) {
                mWindowManager.updateViewLayout(mRoot, mParams);
            }
        }
    };

    @Override
    public void setAnchorView(View view) {
        view.removeOnLayoutChangeListener(mOnLayoutListener);
        mAnchor = view;
        mAnchor.addOnLayoutChangeListener(mOnLayoutListener);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mRoot.setEnabled(enabled);
    }

    @Override
    public void setMediaPlayer(IMediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }


    private void showPopupWindowLayout() {
        updateAnchorViewLayout();
        mWindowManager.addView(mRoot, mParams);
    }

    private void updateAnchorViewLayout() {
        assert mAnchor != null;
        int[] anchorPos = new int[2];
        mAnchor.getLocationOnScreen(anchorPos);
        mRoot.measure(View.MeasureSpec.makeMeasureSpec(mAnchor.getWidth(), View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(mAnchor.getHeight(), View.MeasureSpec.AT_MOST));
        mParams.width = mAnchor.getWidth();
        mParams.height = mRoot.getMeasuredHeight();
        int x = anchorPos[0];
        // TODO: 2/8/17 Weird position when setting videoView in WRAP_CONTENT
        int y = anchorPos[1] + mAnchor.getHeight() - mRoot.getMeasuredHeight();

        mParams.x = x;
        mParams.y = y;
    }

    @Override
    public void show() {
        if (!mIsShowing && mAnchor != null) {
            mIsShowing = true;
            setProgress();
            mRoot.post(mShowProgress);
            updatePausePlay();
            if (mPauseButton != null) {
                mPauseButton.requestFocus();
            }

            showPopupWindowLayout();
        }
    }


    @Override
    public boolean isShowing() {
        return mIsShowing;
    }


}
