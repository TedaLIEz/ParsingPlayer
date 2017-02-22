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

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.AudioManager;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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
    private boolean mTargetFullScreen = false;
    private boolean mTargetTinyScreen = false;

    private double mTotalPositionChanged;

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
        mVolume = getCurrentVolume();
        mControllerView.setRestoreListener(mRestoreListener);
        initInfoProgressBar(context);
        initProgressBar(context);
        initSeekTextView(context);
    }


    private int getCurrentVolume() {
        AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        return am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }


    private void initSeekTextView(Context context) {
        mTextView = new TextView(context);
        mTextView.setBackgroundColor(getResources().getColor(R.color.panel_slide_background));
        int padding = Util.getScreenWidth(context);
        mTextView.setPadding(padding / 50, padding / 50, padding / 50, padding / 50);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mTextView, lp);
        mTextView.setTextColor(getResources().getColor(android.R.color.white));
        mTextView.setTextSize(14);
        mTextView.setVisibility(GONE);
    }

    private void initProgressBar(Context context) {
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
            if (a != null)
                a.recycle();
        }
    }

    public void play(String url) {
        mUrl = url;
        mMedia.play(url);
    }

    public void setRestoreListener(@Nullable OnClickListener restoreListener) {
        mControllerView.setRestoreListener(restoreListener);
    }

    private View.OnClickListener mRestoreListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showFullscreen();
        }
    };


    public void setTargetTiny() {
        mTargetTinyScreen = true;
    }

    private void updatePosition(float dx) {
        int totalTimeDuration = mMedia.getDuration();
        int currentPos = mMedia.getCurrentPosition();
        mTotalPositionChanged = dx * 3 * totalTimeDuration / getWidth() + mTotalPositionChanged;
        mSeekWhenPrepared = Math.min(currentPos + (int) (mTotalPositionChanged), totalTimeDuration);
        showSeekTextView(mSeekWhenPrepared, totalTimeDuration);
    }

    private void dismissSeekTextView() {
        if (mTextView != null) mTextView.setVisibility(GONE);
    }

    private void dismissBufferingProgress() {
        mProgressBar.setVisibility(GONE);
    }

    private void showSeekTextView(int seekPos, int totalDuration) {
        if (!mTextView.isShown()) {
            mTextView.setVisibility(VISIBLE);
        }
        mTextView.setText(String.format(getResources().getString(R.string.seekTime),
                Util.stringForTime(seekPos), Util.stringForTime(totalDuration)));
    }

    private void showBufferingProgress() {
        dismissSeekTextView();
        mProgressBar.setVisibility(VISIBLE);
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
        mTargetFullScreen = true;
    }

    public void onResume() {
        mMedia.setStateChangeListener(this);
        mMedia.onResume(mRenderView);
        mBrightness = mMedia.getCurrentBrightness();
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.screenBrightness = (float) mBrightness;
        ((Activity) mContext).getWindow().setAttributes(lp);
    }

    public void onPause() {
        onBufferingEnd();
        if (mTargetFullScreen) {
            mTargetFullScreen = false;
            return;
        }
        if (mTargetTinyScreen)
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
    // FIXME: 2/21/17 Can't restore progress bar when switch renderView
    public void onBufferingStart() {
        LogUtil.d(TAG, "onBufferingStart, " + toString());
        showBufferingProgress();
    }

    @Override
    public void onBufferingEnd() {
        LogUtil.d(TAG, "onBufferingEnd, " + toString());
        dismissBufferingProgress();
    }


    private double mVolume;
    @Override
    public void onVolumeChange(float dy) {
        AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolume = dy * 4f / getHeight() * maxVolume + mVolume;
        mVolume = Math.min(maxVolume, Math.max(mVolume, 0));
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) mVolume, 0);
        double volumePercent = mVolume / maxVolume;
        int progress = (int) Math.min(Math.max(0, volumePercent * 100), 100);
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
    public void onBrightnessDismiss() {
        if (mBrightProgress != null) mBrightProgress.setVisibility(GONE);
    }

    private double mBrightness;
    @Override
    public void onBrightnessChange(float dy) {
        mBrightness += dy * 4f / getHeight();
        WindowManager.LayoutParams lp = ((Activity) getContext()).getWindow().getAttributes();
        mBrightness = Math.min(Math.max(0f, mBrightness), 1f);
        lp.screenBrightness = (float) mBrightness;
        ((Activity) getContext()).getWindow().setAttributes(lp);
        mMedia.setCurrentBrightness(mBrightness);
        int progress = (int) (lp.screenBrightness * 100);
        if (!mBrightProgress.isShown()) {
            mBrightProgress.setVisibility(VISIBLE);
        }
        mBrightProgress.setProgress(progress);
    }

    @Override
    public void onTogglePlayingState() {
        toggleMediaControlsVisibility();
    }

    @Override
    public void onUpdatePosition(float dx) {
        updatePosition(dx);
    }

    @Override
    public void onSeekToPosition() {
        mMedia.seekTo(mSeekWhenPrepared);
        mTotalPositionChanged = 0;
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
