package com.hustunique.parsingplayer.player.media;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hustunique.parsingplayer.parser.provider.Quality;
import com.hustunique.parsingplayer.player.view.EGLPosterRendererThread;
import com.hustunique.parsingplayer.player.view.IMediaPlayerControl;
import com.hustunique.parsingplayer.player.view.IRenderView;
import com.hustunique.parsingplayer.player.view.TextureRenderView;
import com.hustunique.parsingplayer.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by CoXier on 17-2-19.
 * Manager managing MediaPlayer and RenderView
 */
// TODO: 2/19/17 Refactor on this class
public class ParsingMediaManager implements ParsingPlayerProxy.OnStateListener, IMediaPlayerControl {
    private static final String TAG = "ParsingMediaManager";

    private int mCurrentAspectRatio = IRenderView.AR_ASPECT_FIT_PARENT;

    private TextureRenderView mRenderView;

    private static ParsingMediaManager mManager;
    private ParsingPlayerProxy mCurrentPlayerProxy;
    private Map<String, ParsingPlayerProxy> mPlayerMap;
    private Context mContext;

    private ParsingMediaManager(Context context) {
        mPlayerMap = new HashMap<>();
        mContext = context;
    }

    public static ParsingMediaManager getInstance(Context context) {
        if (mManager == null)
            mManager = new ParsingMediaManager(context);
        return mManager;
    }

    private int mSurfaceWidth, mSurfaceHeight;
    private IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                LogUtil.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }
            LogUtil.d(TAG, "onSurfaceCreated: current renderView" + mRenderView);
            LogUtil.d(TAG, "onSurfaceCreated: current thumbnail: " + mBitmap);
            if (mBitmap != null && !isPlaying()) {
                LogUtil.d(TAG, "rending : " + mBitmap);
                new EGLPosterRendererThread(mBitmap, false, holder.getSurfaceTexture()).start();
            }
            if (mCurrentPlayerProxy != null)
                bindSurfaceHolder(mCurrentPlayerProxy.getPlayer(), holder);
        }

        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                LogUtil.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            boolean isValidState = mCurrentPlayerProxy.isInPlayBackState();
            boolean hasValidSize = !mRenderView.shouldWaitForResize();
            if (mCurrentPlayerProxy != null && isValidState && hasValidSize) {
                mCurrentPlayerProxy.start();
            }
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                LogUtil.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }
            LogUtil.v(TAG, "onSurfaceDestroyed: current renderView" + mRenderView);
            releaseRenderView();
        }
    };
    private Bitmap mBitmap;
    private void releaseRenderView() {
        if (mRenderView == null) return;
        LogUtil.d(TAG, "release current renderView: " + mRenderView +
                "\ncurrent bitmap " + mBitmap);
        if (mCurrentPlayerProxy != null) {
            mCurrentPlayerProxy.setCurrentDisplay(null);
        }
        // Clear display
        mRenderView.removeRenderCallback(mSHCallback);
        mRenderView = null;

    }

    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null) return;
        if (holder == null) {
            mp.setDisplay(null);
            return;
        }
        holder.bindToMediaPlayer(mp);
    }

    public void configureRenderView(TextureRenderView renderView) {
        if (renderView == null) throw new IllegalArgumentException("Render view can't be null");
        LogUtil.d(TAG, "configure renderView: " + renderView);
        releaseRenderView();
        mRenderView = renderView;
        if (getCurrentVideoWidth() > 0 && getCurrentVideoHeight() > 0) {
            // this will call if the current activity onDestroyed called, then we resume the activity
            // we will check video specs, if we have, then restore them.
            mRenderView.setVideoSize(getCurrentVideoWidth(), getCurrentVideoHeight());
        }
        mRenderView.setAspectRatioMode(mCurrentAspectRatio);
        mRenderView.addRenderCallback(mSHCallback);
    }

    @Override
    public void onPrepared(int videoWidth, int videoHeight, int videoSarNum, int videoSarDen) {
        mRenderView.setVideoSize(videoWidth, videoHeight);
        mRenderView.setVideoSampleAspectRatio(videoSarNum, videoSarDen);

        if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == videoWidth
                || mSurfaceHeight == videoHeight) {
            mCurrentPlayerProxy.start();
            if (!mCurrentPlayerProxy.isPlaying() && (mCurrentPlayerProxy.getCurrentPosition() > 0)) {
                if (mStateChangeListener != null) mStateChangeListener.onPrepared();
            }
        }
    }

    @Override
    public void onVideoSizeChanged(int videoWidth, int videoHeight, int videoSarNum, int videoSarDen) {
        mRenderView.setVideoSize(videoWidth, videoHeight);
        mRenderView.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
    }


    public void onResume(TextureRenderView renderView) {
        LogUtil.v(TAG, "onResume: current view " + Integer.toHexString(System.identityHashCode(mRenderView))
                + ", target view: " + Integer.toHexString(System.identityHashCode(renderView)));
        if (mRenderView == renderView) return;
        configureRenderView(renderView);
    }

    /**
     * release specific player playing url
     *
     * @param url
     */
    public void onDestroy(String url) {
        destroyPlayerByURL(url);
    }


    /**
     * Get current width of video
     *
     * @return -1 if there is no video on play, else an integer represents the current video's width
     */
    public int getCurrentVideoWidth() {
        return mCurrentPlayerProxy != null ? mCurrentPlayerProxy.getVideoWidth() : -1;
    }

    /**
     * Get current height of video
     *
     * @return -1 if there is no video on play, else an integer represents the current video's height
     */
    public int getCurrentVideoHeight() {
        return mCurrentPlayerProxy != null ? mCurrentPlayerProxy.getVideoHeight() : -1;
    }


    public int getCurrentVideoSarDen() {
        return mCurrentPlayerProxy != null ? mCurrentPlayerProxy.getVideoSarDen() : -1;
    }


    public int getCurrentVideoSarNum() {
        return mCurrentPlayerProxy != null ? mCurrentPlayerProxy.getVideoSarNum() : -1;
    }

    public void setStateChangeListener(@Nullable MediaStateChangeListener stateChangeListener) {
        mStateChangeListener = stateChangeListener;
    }


    private MediaStateChangeListener mStateChangeListener;

    @Override
    public void start() {
        mCurrentPlayerProxy.start();
    }

    @Override
    public void pause() {
        mBitmap = mRenderView.getBitmap();
        LogUtil.d(TAG, "video paused, cache thumbnail: " + mBitmap);
        mCurrentPlayerProxy.pause();
    }

    @Override
    public int getDuration() {
        return mCurrentPlayerProxy.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mCurrentPlayerProxy.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mCurrentPlayerProxy.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mCurrentPlayerProxy.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mCurrentPlayerProxy.getBufferPercentage();
    }

    public void play(String videoUrl) {
        if (mPlayerMap.containsKey(videoUrl)) {
            mCurrentPlayerProxy = mPlayerMap.get(videoUrl);
        } else {
            mCurrentPlayerProxy = new ParsingPlayerProxy(mContext, this);
            mPlayerMap.put(videoUrl, mCurrentPlayerProxy);
        }
        mCurrentPlayerProxy.play(videoUrl);
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onCompleted() {
        if (mStateChangeListener != null) mStateChangeListener.onPlayCompleted();
    }

    @Override
    public void onError(String msg) {
        if (mStateChangeListener != null) mStateChangeListener.onError(msg);
    }

    @Override
    public void onInfo(int arg1) {
        if (arg1 == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            mStateChangeListener.onBufferingStart();
        } else if (arg1 == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            mStateChangeListener.onBufferingEnd();
        }
    }


    /**
     * Change video quality in {@link Quality}
     *
     * @param quality the quality, specified in {@link Quality}
     */
    public void setQuality(@Quality int quality) {

    }

    private void destroyPlayerByURL(String url) {
        if (mPlayerMap.containsKey(url)) {
            ParsingPlayerProxy player = mPlayerMap.get(url);
            player.release();
            LogUtil.w(TAG, "release player " + player);
            mPlayerMap.remove(url);
        } else
            throw new IllegalArgumentException("no player match this url ");
    }

    public double getCurrentBrightness() {
        return mCurrentPlayerProxy == null ? ((Activity) mContext).getWindow().getAttributes().screenBrightness
                : mCurrentPlayerProxy.getBrightness();
    }

    public void setCurrentBrightness(@FloatRange(from = 0f, to = 1f) double brightness) {
        mCurrentPlayerProxy.setBrightness(brightness);
    }

}
