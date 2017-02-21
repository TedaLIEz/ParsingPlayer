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
import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hustunique.parsingplayer.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.ISurfaceTextureHolder;
import tv.danmaku.ijk.media.player.ISurfaceTextureHost;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureRenderView extends TextureView implements IRenderView, SimpleGestureListener.Listener {
    private static final String TAG = "TextureRenderView";
    private static final float MINIMUM_BRIGHTNESS = 0.04f;
    private MeasureHelper mMeasureHelper;

    private GestureDetector mGestureDetector;

    private double mVolume;
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
        mSurfaceCallback = new SurfaceCallback(this);
        setSurfaceTextureListener(mSurfaceCallback);
        SimpleGestureListener gestureListener = new SimpleGestureListener();
        mGestureDetector = new GestureDetector(context, gestureListener);
        gestureListener.setListener(this);
        mVolume = getCurrentVolume();
    }


    @Override
    public boolean shouldWaitForResize() {
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        mSurfaceCallback.willDetachFromWindow();
        super.onDetachedFromWindow();
        mSurfaceCallback.didDetachFromWindow();
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
            updateBrightness(dy);
        else
            updateVolume(dy);
        mVolumeOrBrightnessChanged = true;
    }

    @Override
    public void onClick() {
        mOnVideoChangeListener.onTogglePlayingState();
    }

    public interface OnVideoChangeListener {
        void onVolumeDialogShow(int volumePercent);

        void onVolumeDialogDismiss();

        void onBrightnessShow(int brightness);

        void onBrightnessDismiss();

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

    private int getCurrentBrightness() {
        float defaultBrightness = ((Activity) getContext()).getWindow().getAttributes().screenBrightness;
        if (Float.compare(defaultBrightness, 0) < 0) {
            int brightness = 0;
            try {
                brightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            } catch (Settings.SettingNotFoundException e) {
                LogUtil.wtf(TAG, e);
            }
            return brightness;
        } else {
            return (int) (defaultBrightness * 255);
        }
    }

    private int getCurrentVolume() {
        AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        return am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    private void updateBrightness(float dy) {
        int gestureDownBrightness = getCurrentBrightness();
        float deltaV = 255 * dy * 3 / getHeight();
        WindowManager.LayoutParams lp = ((Activity) getContext()).getWindow().getAttributes();
        lp.screenBrightness = Math.min(Math.max(0, (gestureDownBrightness + deltaV) / 255), 1);
        ((Activity) getContext()).getWindow().setAttributes(lp);
        showBrightnessDialog((int) (lp.screenBrightness * 100));
    }

    private void showBrightnessDialog(int brightnessPercent) {
        if (mOnVideoChangeListener != null) {
            mOnVideoChangeListener.onBrightnessShow(brightnessPercent);
        }
    }

    private void updateVolume(float dy) {
        AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolume = dy * 4f / getHeight() * maxVolume + mVolume;
        mVolume = Math.min(maxVolume, Math.max(mVolume, 0));
        am.setStreamVolume(AudioManager.STREAM_MUSIC, (int) mVolume, 0);
        double volumePercent = mVolume / maxVolume;
        showVolumeDialog((int) (volumePercent * 100));
    }

    private void showVolumeDialog(int volumePercent) {
        if (mOnVideoChangeListener != null) {
            mOnVideoChangeListener.onVolumeDialogShow(volumePercent);
        }
    }


    //--------------------
    // TextureViewHolder
    //--------------------

    public IRenderView.ISurfaceHolder getSurfaceHolder() {
        return new InternalSurfaceHolder(this, mSurfaceCallback.mSurfaceTexture, mSurfaceCallback);
    }


    private static final class InternalSurfaceHolder implements IRenderView.ISurfaceHolder {
        private TextureRenderView mTextureView;
        private SurfaceTexture mSurfaceTexture;
        private ISurfaceTextureHost mSurfaceTextureHost;

        InternalSurfaceHolder(@NonNull TextureRenderView textureView,
                              @Nullable SurfaceTexture surfaceTexture,
                              @NonNull ISurfaceTextureHost surfaceTextureHost) {
            mTextureView = textureView;
            mSurfaceTexture = surfaceTexture;
            mSurfaceTextureHost = surfaceTextureHost;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void bindToMediaPlayer(IMediaPlayer mp) {
            LogUtil.d(TAG, "bind mediaPlayer");
            if (mp == null)
                return;

            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) &&
                    (mp instanceof ISurfaceTextureHolder)) {
                ISurfaceTextureHolder textureHolder = (ISurfaceTextureHolder) mp;
                mTextureView.mSurfaceCallback.setOwnSurfaceTexture(false);

                SurfaceTexture surfaceTexture = textureHolder.getSurfaceTexture();
                if (surfaceTexture != null) {
                    mTextureView.setSurfaceTexture(surfaceTexture);
                } else {
                    textureHolder.setSurfaceTexture(mSurfaceTexture);
                    textureHolder.setSurfaceTextureHost(mTextureView.mSurfaceCallback);
                }
            } else {
                mp.setSurface(openSurface());
            }
        }

        @NonNull
        @Override
        public IRenderView getRenderView() {
            return mTextureView;
        }

        @Nullable
        @Override
        public SurfaceHolder getSurfaceHolder() {
            return null;
        }

        @Nullable
        @Override
        public SurfaceTexture getSurfaceTexture() {
            return mSurfaceTexture;
        }

        @Nullable
        @Override
        public Surface openSurface() {
            if (mSurfaceTexture == null)
                return null;
            LogUtil.d(TAG, "Open new Surface");
            return new Surface(mSurfaceTexture);
        }
    }

    //-------------------------
    // SurfaceHolder.Callback
    //-------------------------

    @Override
    public void addRenderCallback(@NonNull IRenderCallback callback) {
        mSurfaceCallback.addRenderCallback(callback);
    }

    @Override
    public void removeRenderCallback(@NonNull IRenderCallback callback) {
        mSurfaceCallback.removeRenderCallback(callback);
    }

    private SurfaceCallback mSurfaceCallback;

    private static final class SurfaceCallback implements SurfaceTextureListener, ISurfaceTextureHost {
        private SurfaceTexture mSurfaceTexture;
        private boolean mIsFormatChanged;
        private int mWidth;
        private int mHeight;

        private boolean mOwnSurfaceTexture = true;
        private boolean mWillDetachFromWindow = false;
        private boolean mDidDetachFromWindow = false;

        private WeakReference<TextureRenderView> mWeakRenderView;
        private Map<IRenderCallback, Object> mRenderCallbackMap = new ConcurrentHashMap<IRenderCallback, Object>();

        SurfaceCallback(@NonNull TextureRenderView renderView) {
            mWeakRenderView = new WeakReference<>(renderView);
        }

        void setOwnSurfaceTexture(boolean ownSurfaceTexture) {
            mOwnSurfaceTexture = ownSurfaceTexture;
        }

        void addRenderCallback(@NonNull IRenderCallback callback) {
            mRenderCallbackMap.put(callback, callback);

            ISurfaceHolder surfaceHolder = null;
            if (mSurfaceTexture != null) {
                if (surfaceHolder == null)
                    surfaceHolder = new InternalSurfaceHolder(mWeakRenderView.get(), mSurfaceTexture, this);
                callback.onSurfaceCreated(surfaceHolder, mWidth, mHeight);
            }

            if (mIsFormatChanged) {
                if (surfaceHolder == null)
                    surfaceHolder = new InternalSurfaceHolder(mWeakRenderView.get(), mSurfaceTexture, this);
                callback.onSurfaceChanged(surfaceHolder, 0, mWidth, mHeight);
            }
        }

        void removeRenderCallback(@NonNull IRenderCallback callback) {
            mRenderCallbackMap.remove(callback);
        }

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mSurfaceTexture = surface;
            mIsFormatChanged = false;
            mWidth = 0;
            mHeight = 0;

            ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakRenderView.get(), surface, this);
            LogUtil.w(TAG, "callback size " + mRenderCallbackMap.size());
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceCreated(surfaceHolder, 0, 0);
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            mSurfaceTexture = surface;
            mIsFormatChanged = true;
            mWidth = width;
            mHeight = height;

            ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakRenderView.get(), surface, this);
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceChanged(surfaceHolder, 0, width, height);
            }
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mSurfaceTexture = surface;
            mIsFormatChanged = false;
            mWidth = 0;
            mHeight = 0;

            ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakRenderView.get(), surface, this);
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceDestroyed(surfaceHolder);
            }

            Log.d(TAG, "onSurfaceTextureDestroyed: destroy: " + mOwnSurfaceTexture);
            return mOwnSurfaceTexture;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }

        //-------------------------
        // ISurfaceTextureHost
        //-------------------------

        @Override
        public void releaseSurfaceTexture(SurfaceTexture surfaceTexture) {
            if (surfaceTexture == null) {
                Log.d(TAG, "releaseSurfaceTexture: null");
            } else if (mDidDetachFromWindow) {
                if (surfaceTexture != mSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: didDetachFromWindow(): release different SurfaceTexture");
                    surfaceTexture.release();
                } else if (!mOwnSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: didDetachFromWindow(): release detached SurfaceTexture");
                    surfaceTexture.release();
                } else {
                    Log.d(TAG, "releaseSurfaceTexture: didDetachFromWindow(): already released by TextureView");
                }
            } else if (mWillDetachFromWindow) {
                if (surfaceTexture != mSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: willDetachFromWindow(): release different SurfaceTexture");
                    surfaceTexture.release();
                } else if (!mOwnSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: willDetachFromWindow(): re-attach SurfaceTexture to TextureView");
                    setOwnSurfaceTexture(true);
                } else {
                    Log.d(TAG, "releaseSurfaceTexture: willDetachFromWindow(): will released by TextureView");
                }
            } else {
                if (surfaceTexture != mSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: alive: release different SurfaceTexture");
                    surfaceTexture.release();
                } else if (!mOwnSurfaceTexture) {
                    Log.d(TAG, "releaseSurfaceTexture: alive: re-attach SurfaceTexture to TextureView");
                    setOwnSurfaceTexture(true);
                } else {
                    Log.d(TAG, "releaseSurfaceTexture: alive: will released by TextureView");
                }
            }
        }

        void willDetachFromWindow() {
            Log.d(TAG, "willDetachFromWindow()");
            mWillDetachFromWindow = true;
        }

        void didDetachFromWindow() {
            Log.d(TAG, "didDetachFromWindow()");
//            mSurfaceTexture.release();
            mDidDetachFromWindow = true;
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
