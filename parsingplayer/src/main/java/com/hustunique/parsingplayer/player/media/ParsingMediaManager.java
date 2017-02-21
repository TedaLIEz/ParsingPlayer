package com.hustunique.parsingplayer.player.media;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.hustunique.parsingplayer.parser.provider.Quality;
import com.hustunique.parsingplayer.player.view.IMediaPlayerControl;
import com.hustunique.parsingplayer.player.view.IRenderView;
import com.hustunique.parsingplayer.player.view.TextureRenderView;
import com.hustunique.parsingplayer.util.LogUtil;

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
    private ParsingPlayerProxy mPlayerManager;
    private ParsingMediaManager(Context context) {
        mPlayerManager = new ParsingPlayerProxy(context, this);
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
            if (mPlayerManager != null)
                bindSurfaceHolder(mPlayerManager.getCurrentPlayer(), holder);
        }

        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                LogUtil.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            boolean isValidState = mPlayerManager.isInPlayBackState();
            boolean hasValidSize = !mRenderView.shouldWaitForResize();
            if (mPlayerManager != null && isValidState && hasValidSize) {
                mPlayerManager.start();
            }
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                LogUtil.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }

            releaseRenderView();
        }
    };

    private void releaseRenderView() {
        if (mRenderView == null) return;
        if (mPlayerManager != null) {
            mPlayerManager.setCurrentDisplay(null);
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
            mPlayerManager.start();
            if (!mPlayerManager.isPlaying() && (mPlayerManager.getCurrentPosition() > 0)) {
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
        configureRenderView(renderView);
        mPlayerManager.start();
    }

    /**
     * release specific player playing url
     * @param url
     */
    public void onDestroy(String url){
//        mPlayerManager.destroyPlayerByURL(url);
        mPlayerManager.reset();
    }


    /**
     * Get current width of video
     *
     * @return -1 if there is no video on play, else an integer represents the current video's width
     */
    public int getCurrentVideoWidth() {
        return mPlayerManager != null ? mPlayerManager.getVideoWidth() : -1;
    }

    /**
     * Get current height of video
     *
     * @return -1 if there is no video on play, else an integer represents the current video's height
     */
    public int getCurrentVideoHeight() {
        return mPlayerManager != null ? mPlayerManager.getVideoHeight() : -1;
    }


    public int getCurrentVideoSarDen() {
        return mPlayerManager != null ? mPlayerManager.getVideoSarDen() : -1;
    }


    public int getCurrentVideoSarNum() {
        return mPlayerManager != null ? mPlayerManager.getVideoSarNum() : -1;
    }

    public void setStateChangeListener(@Nullable MediaStateChangeListener stateChangeListener) {
        mStateChangeListener = stateChangeListener;
    }


    private MediaStateChangeListener mStateChangeListener;

    @Override
    public void start() {
        mPlayerManager.start();
    }

    @Override
    public void pause() {
        mPlayerManager.pause();
    }

    @Override
    public int getDuration() {
        return mPlayerManager.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mPlayerManager.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mPlayerManager.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mPlayerManager.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mPlayerManager.getBufferPercentage();
    }

    public void play(String videoUrl) {
        mPlayerManager.play(videoUrl);
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
    public void onInfo() {

    }


    /**
     * Change video quality in {@link Quality}
     *
     * @param quality the quality, specified in {@link Quality}
     */
    public void setQuality(@Quality int quality) {

    }

    @VisibleForTesting
    public boolean isIdle() {
        return getCurrentVideoHeight() <= 0 && getCurrentVideoWidth() <= 0;
    }
}
