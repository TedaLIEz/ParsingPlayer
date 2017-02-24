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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hustunique.parsingplayer.util.LogUtil;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureRenderView extends TextureView implements IRenderView, SimpleGestureListener.Listener {
    private static final String TAG = "TextureRenderView";
    private MeasureHelper mMeasureHelper;

    private GestureDetector mGestureDetector;

    private boolean mPositionChanged;
    private boolean mVolumeOrBrightnessChanged;


    public TextureRenderView(Context context) {
        this(context, null);
    }

    public TextureRenderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextureRenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextureRenderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        mMeasureHelper = new MeasureHelper(this);
        mSurfaceCallback = new SurfaceCallback();
        setSurfaceTextureListener(mSurfaceCallback);
        SimpleGestureListener gestureListener = new SimpleGestureListener();
        mGestureDetector = new GestureDetector(context, gestureListener);
        gestureListener.setListener(this);

    }


    @Override
    public boolean shouldWaitForResize() {
        return false;
    }



    //--------------------
    // Layout & Measure
    //--------------------
    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        if (videoSarNum > 0 && videoSarDen > 0) {
            mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
            requestLayout();
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        mMeasureHelper.setVideoRotation(degree);
        setRotation(degree);
    }

    @Override
    public void setAspectRatioMode(int aspectRatio) {
        mMeasureHelper.setAspectRatioMode(aspectRatio);
        requestLayout();
    }

    @Override
    public void setAspectRatio(float aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }

    @Override
    public void onScrollHorizontal(float dx) {
        if (mVolumeOrBrightnessChanged) return;
        mOnVideoChangeListener.onUpdatePosition(-dx);
        mPositionChanged = true;
    }

    @Override
    public void onScrollVertical(float x, float dy) {
        if (mPositionChanged) return;
        if (x < getWidth() / 2)
            mOnVideoChangeListener.onBrightnessChange(dy);
        else
            mOnVideoChangeListener.onVolumeChange(dy);
        mVolumeOrBrightnessChanged = true;
    }

    @Override
    public void onClick() {
        mOnVideoChangeListener.onTogglePlayingState();
    }

    public interface OnVideoChangeListener {

        void onVolumeChange(float dy);

        void onVolumeDialogDismiss();


        void onBrightnessDismiss();

        void onBrightnessChange(float dy);
        void onTogglePlayingState();

        void onUpdatePosition(float dx);

        void onSeekToPosition();
    }

    private OnVideoChangeListener mOnVideoChangeListener;

    public void setOnVideoChangeListener(@Nullable OnVideoChangeListener onVideoChangeListener) {
        mOnVideoChangeListener = onVideoChangeListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_UP) {
            if (mPositionChanged) {
                mOnVideoChangeListener.onSeekToPosition();
                mPositionChanged = false;
            } else {
                dismissBrightnessDialog();
                dismissVolumeDialog();
                mVolumeOrBrightnessChanged = false;
            }

        }
        return true;
    }

    private void dismissBrightnessDialog() {
        if (mOnVideoChangeListener != null) {
            mOnVideoChangeListener.onBrightnessDismiss();
        }
    }

    private void dismissVolumeDialog() {
        if (mOnVideoChangeListener != null) {
            mOnVideoChangeListener.onVolumeDialogDismiss();
        }
    }



    public void setRenderCallback(IRenderView.IRenderCallback callback) {
        mSurfaceCallback.setRenderCallback(callback);
    }


    private SurfaceCallback mSurfaceCallback;

    private static final class SurfaceCallback implements SurfaceTextureListener {

        private boolean mOwnSurfaceTexture = true;

        private IRenderView.IRenderCallback mRenderCallback;

        SurfaceCallback() {

        }


        void setRenderCallback(@NonNull IRenderView.IRenderCallback callback) {
            mRenderCallback = callback;
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            LogUtil.v(TAG, "onSurfaceTextureAvailable" + surface);
            mRenderCallback.onSurfaceCreated(surface, 0, 0);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            mRenderCallback.onSurfaceChanged(surface, 0, width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {

            mRenderCallback.onSurfaceDestroyed(surface);
            LogUtil.v(TAG, "onSurfaceTextureDestroyed: destroy: " + mOwnSurfaceTexture);
            return mOwnSurfaceTexture;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }

    }

    //--------------------
    // Accessibility
    //--------------------

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(TextureRenderView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(TextureRenderView.class.getName());
    }
}
