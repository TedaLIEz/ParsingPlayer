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
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hustunique.parsingplayer.R;
import com.hustunique.parsingplayer.player.android.ParsingIntegrator;
import com.hustunique.parsingplayer.player.media.MediaStateChangeListener;
import com.hustunique.parsingplayer.player.media.ParsingMediaManager;
import com.hustunique.parsingplayer.util.LogUtil;
import com.hustunique.parsingplayer.util.Util;

/**
 * Created by JianGuo on 1/16/17.
 * VideoView using {@link tv.danmaku.ijk.media.player.IMediaPlayer} as media player
 */

public class ParsingVideoView extends RelativeLayout implements MediaStateChangeListener, TextureRenderView.OnVideoChangeListener {
    private static final String TAG = "ParsingVideoView";
    private float mSlop;
    private static final float SET_PROGRESS_VERTICAL_SLIP = 10f;

    private Context mContext;

    private ParsingMediaManager mMedia;
    private ControllerView mControllerView;
    private int mSeekWhenPrepared;
    private TextureRenderView mRenderView;
    private ProgressSlideView mVolumeProgress, mBrightProgress;
    private ProgressBar mProgressBar;
    private TextView mTextView;
    private String mUrl;

    private boolean mFullscreen;
    private boolean mTargetFullscreen = false;
    private boolean mTargetTinyscreen = false;

    public ParsingVideoView(Context context) {
        this(context, null);
    }

    public ParsingVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParsingVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ParsingVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mContext = context;
        getFullscreen(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.parsing_video_view, this);
        mRenderView = (TextureRenderView) findViewById(R.id.texture_view);
        mControllerView = (ControllerView) findViewById(R.id.controller_view);
        mMedia = ParsingMediaManager.getInstance(mContext);
        mMedia.configureRenderView(mRenderView);
        mControllerView.setMediaPlayer(mMedia);
        mMedia.setStateChangeListener(this);
        mControllerView.setRestoreListener(mRestoreListener);
        mRenderView.setOnClickListener(mRenderViewClickListener);
        initInfoProgressBar(context);
        initControlPanel(context);
        initSeekTextView(context);
    }

    private void initSeekTextView(Context context) {
        mTextView = new TextView(context);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mTextView, lp);
        mTextView.setTextColor(getResources().getColor(android.R.color.white));
        mTextView.setTextSize(14);
        mTextView.setVisibility(GONE);
    }

    private void initControlPanel(Context context) {
        mProgressBar = new ProgressBar(context);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mProgressBar, lp);
        mProgressBar.setVisibility(GONE);
    }

    private void initInfoProgressBar(Context context) {
        LayoutParams volumeParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        volumeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        volumeParams.addRule(RelativeLayout.ALIGN_PARENT_START, TRUE);
        volumeParams.setMarginStart(Util.getScreenWidth(context) / 10);
        LayoutParams brightnessParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        brightnessParams.addRule(RelativeLayout.CENTER_VERTICAL);
        brightnessParams.addRule(RelativeLayout.ALIGN_PARENT_END, TRUE);
        brightnessParams.setMarginEnd(Util.getScreenWidth(context) / 10);
        mVolumeProgress = ProgressSlideView.createView(context, volumeParams, R.drawable.ic_volume_2);
        mBrightProgress = ProgressSlideView.createView(context, brightnessParams, R.drawable.ic_brightness_2);
        addView(mVolumeProgress);
        addView(mBrightProgress);
        mVolumeProgress.setVisibility(GONE);
        mBrightProgress.setVisibility(GONE);
        mRenderView.setOnVideoChangeListener(this);
    }

    private void getFullscreen(Context context, AttributeSet attrs) {
        TypedArray a = null;
        try {
            a = context.obtainStyledAttributes(attrs, R.styleable.ParsingVideoViewTheme);
            mFullscreen = a.getBoolean(R.styleable.ParsingVideoViewTheme_fullscreen, false);
        } finally {
            a.recycle();
        }
    }

    public void setRestoreListener(OnClickListener restoreListener) {
        mControllerView.setRestoreListener(restoreListener);
    }

    private View.OnClickListener mRestoreListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showFullscreen();
        }
    };

    private View.OnClickListener mRenderViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleMediaControlsVisibility();
        }
    };

    public void setTargetTiny(){
        mTargetTinyscreen = true;
    }

    private float mDownX, mDownY;
    private boolean mChangePos = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                mChangePos = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final float dx = ev.getX() - mDownX;
                final float dy = ev.getY() - mDownY;
                float absDx = Math.abs(dx);
                float absDy = Math.abs(dy);
                if (checkValidSlide(absDx, absDy)) {
                    mChangePos = true;
                    updatePosition(dx, mMedia.getCurrentPosition());
                }
                break;
            case MotionEvent.ACTION_UP:
                dismissProgressBar();
                dismissSeekTextView();
                if (mChangePos) {
                    mMedia.seekTo(mSeekWhenPrepared);
                }
                break;
        }
        return this.mChangePos || super.dispatchTouchEvent(ev);
    }

    private void dismissSeekTextView() {
        if (mTextView != null) mTextView.setVisibility(GONE);
    }

    private void dismissProgressBar() {
        if (mProgressBar != null) mProgressBar.setVisibility(GONE);
    }

    private boolean checkValidSlide(float absDx, float absDy) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(mContext);
        mSlop = viewConfiguration.getScaledTouchSlop();
        return Float.compare(absDx, mSlop) > 0 && Float.compare(absDy, SET_PROGRESS_VERTICAL_SLIP) < 0;
    }

    private void updatePosition(float dx, int currentPos) {
        int totalTimeDuration = mMedia.getDuration();
        mSeekWhenPrepared = Math.min((int) ((currentPos + dx * totalTimeDuration) / getWidth()), totalTimeDuration);
        showSeekTextView(mSeekWhenPrepared, totalTimeDuration);
    }

    private void showSeekTextView(int seekPos, int totalDuration) {
        if (!mTextView.isShown()) {
            mTextView.setVisibility(VISIBLE);
        }
        mTextView.setText(String.format(getResources().getString(R.string.seekTime),
                Util.stringForTime(seekPos), Util.stringForTime(totalDuration)));
    }

    private void showBufferingProgress() {
        if (!mProgressBar.isShown()) {
            mProgressBar.setVisibility(VISIBLE);
        }
    }

    public void play(String url) {
        mUrl = url;
        mMedia.play(url);
    }


    private void toggleMediaControlsVisibility() {
        if (mControllerView.isShowing()) {
            mControllerView.hide();
        } else {
            mControllerView.show();
        }
    }


    private void showFullscreen() {
        ParsingIntegrator parsingIntegrator = new ParsingIntegrator(mContext);
        parsingIntegrator.parsingToPlay();
        mTargetFullscreen = true;
    }

    public void onResume() {
        mMedia.onResume(mRenderView);
    }

    public void onPause() {
        if (mTargetFullscreen) {
            mTargetFullscreen = false;
            return;
        }
        if (mTargetTinyscreen)
            return;
        mMedia.pause();
    }

    public void onDestroy() {
        if (mFullscreen) return;
        mMedia.onDestroy(mUrl);
    }

    @Override
    public void onPrepared() {
        mControllerView.show();
    }

    @Override
    public void onError(String msg) {
        // TODO: 2/19/17 Error Handling
    }

    @Override
    public void onPlayCompleted() {
        mControllerView.complete();
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (mControllerView != null && visibility == GONE) {
            mControllerView.hide();
        }
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        LogUtil.d(TAG, "onRestoreInstanceState " + ss.toString());
        mRenderView.setVideoSize(ss.mVideoWidth, ss.mVideoHeight);
        mRenderView.setVideoSampleAspectRatio(ss.mVideoSarNum, ss.mVideoSarDen);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState ss = new SavedState(parcelable);
        ss.mVideoWidth = mMedia.getCurrentVideoWidth();
        ss.mVideoHeight = mMedia.getCurrentVideoHeight();
        ss.mVideoSarDen = mMedia.getCurrentVideoSarDen();
        ss.mVideoSarNum = mMedia.getCurrentVideoSarNum();

        LogUtil.d(TAG, "onSaveInstanceState " + ss.toString());
        return ss;
    }

    @Override
    public void onVolumeDialogShow(int volumePercent) {
        int progress = Math.min(Math.max(0, volumePercent), 100);
        if (!mVolumeProgress.isShown()) {
            mVolumeProgress.setVisibility(VISIBLE);
        }
        mVolumeProgress.setProgress(progress);
    }

    @Override
    public void onVolumeDialogDismiss() {
        if (mVolumeProgress != null) mVolumeProgress.setVisibility(GONE);
    }

    @Override
    public void onBrightnessShow(int brightness) {
        int progress = Math.min(Math.max(0, brightness), 100);
        if (!mBrightProgress.isShown()) {
            mBrightProgress.setVisibility(VISIBLE);
        }
        mBrightProgress.setProgress(progress);
    }

    @Override
    public void onBrightnessDismiss() {
        if (mBrightProgress != null) mBrightProgress.setVisibility(GONE);
    }

    static class SavedState extends BaseSavedState {
        private int mVideoWidth, mVideoHeight;
        private int mVideoSarNum, mVideoSarDen;
        private ClassLoader mClassLoader;

        SavedState(Parcel in) {
            super(in);
            mClassLoader = getClass().getClassLoader();
            mVideoWidth = in.readInt();
            mVideoHeight = in.readInt();
            mVideoSarNum = in.readInt();
            mVideoSarDen = in.readInt();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public SavedState(Parcel source, ClassLoader loader) {
            super(source, loader);
            if (loader == null) {
                loader = getClass().getClassLoader();
            }
            mClassLoader = loader;
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mVideoWidth);
            out.writeInt(mVideoHeight);
            out.writeInt(mVideoSarNum);
            out.writeInt(mVideoSarDen);
        }

        @Override
        public String toString() {
            return "SavedState{" +
                    "mVideoWidth=" + mVideoWidth +
                    ", mVideoHeight=" + mVideoHeight +
                    ", mVideoSarNum=" + mVideoSarNum +
                    ", mVideoSarDen=" + mVideoSarDen +
                    '}';
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };

    }

}
