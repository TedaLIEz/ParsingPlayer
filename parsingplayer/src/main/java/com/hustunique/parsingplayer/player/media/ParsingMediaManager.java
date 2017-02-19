package com.hustunique.parsingplayer.player.media;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    private static final int DEFAULT_PLAY_MODE = 1;
    private static final int FULLSCREEN_PLAY_MODE = 1 << 2;
    private int flag = DEFAULT_PLAY_MODE;
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


    /**
     * Change video quality in {@link Quality}
     *
     * @param quality the quality, specified in {@link Quality}
     */
    public void setQuality(@Quality int quality) {

    }


    private IRenderView.ISurfaceHolder mSurfaceHolder;
    private int mSurfaceWidth, mSurfaceHeight;
    private IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                LogUtil.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }
            mSurfaceHolder = holder;
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
//                    || (mVideoWidth == width && mVideoHeight == height);
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

            mSurfaceHolder = null;
            release();
        }
    };

    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null) return;
        if (holder == null) {
            mp.setDisplay(null);
            return;
        }
        holder.bindToMediaPlayer(mp);
    }

    private void release() {
        if (mPlayerManager != null) {
            // this will not stop the playing
            mPlayerManager.setCurrentDisplay(null);
        }
    }


    public void configureRenderView(TextureRenderView renderView) {
        if (mRenderView == renderView) return;
        if (mRenderView != null) {
            if (mPlayerManager != null) {
                mPlayerManager.setCurrentDisplay(null);
            }
            // Clear display
            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView = null;
        }
        mRenderView = renderView;
        mRenderView.getSurfaceHolder().bindToMediaPlayer(mPlayerManager.getCurrentPlayer());
        mRenderView.setAspectRatioMode(mCurrentAspectRatio);
        mRenderView.addRenderCallback(mSHCallback);
    }


    public void setStateChangeListener(@Nullable MediaStateChangeListener stateChangeListener) {
        mStateChangeListener = stateChangeListener;
    }


    private MediaStateChangeListener mStateChangeListener;

    @Override
    public void start() {
        if (flag == DEFAULT_PLAY_MODE) {
            mPlayerManager.start();
        }
    }

    @Override
    public void pause() {
        if (flag == DEFAULT_PLAY_MODE) {
            mPlayerManager.pause();
        }
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
    public void onPrepared(int videoWidth, int videoHeight, int videoSarNum, int videoSarDen) {
        if (mRenderView != null) {
            mRenderView.setVideoSize(videoWidth, videoHeight);
            mRenderView.setVideoSampleAspectRatio(videoSarNum, videoSarDen);

            if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == videoWidth
                    || mSurfaceHeight == videoHeight) {
                mPlayerManager.start();
                if (!mPlayerManager.isPlaying() && (mPlayerManager.getCurrentPosition() > 0)) {
                    // TODO: show controllerView
                    if (mStateChangeListener != null) mStateChangeListener.onPrepared();
                }
            }
        }
    }

    @Override
    public void onVideoSizeChanged(int videoWidth, int videoHeight, int videoSarNum, int videoSarDen) {
        if (mRenderView != null) {
            mRenderView.setVideoSize(videoWidth, videoHeight);
            mRenderView.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
        }
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


    public void onResume() {
        start();
    }

    public void onPause() {
        pause();
    }

}
