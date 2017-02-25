package com.hustunique.parsingplayer.player.media;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.view.Surface;

import com.hustunique.parsingplayer.parser.entity.VideoInfo;
import com.hustunique.parsingplayer.parser.provider.Quality;
import com.hustunique.parsingplayer.player.view.IMediaPlayerControl;
import com.hustunique.parsingplayer.player.view.IRenderView;
import com.hustunique.parsingplayer.player.view.TextureRenderView;
import com.hustunique.parsingplayer.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by CoXier on 17-2-19.
 * Manager managing MediaPlayer and RenderView
 */
public class ParsingMediaManager implements ParsingPlayerProxy.OnStateListener, IMediaPlayerControl {
    private static final String TAG = "ParsingMediaManager";

    private WeakReference<TextureRenderView> mRenderView;
    private VideoRenderThread mRenderThread;
    private static ParsingMediaManager mManager;
    private ParsingPlayerProxy mCurrentPlayerProxy;
    private Map<String, ParsingPlayerProxy> mPlayerMap;
    private WeakReference<Context> mContext;

    private ParsingMediaManager(Context context) {
        mPlayerMap = new ConcurrentHashMap<>();
        mContext = new WeakReference<>(context);
        mRenderThread = new VideoRenderThread();
        mRenderThread.start();
        mRenderThread.prepareHandler();
    }

    public static ParsingMediaManager getInstance(Context context) {
        if (mManager == null)
            mManager = new ParsingMediaManager(context);
        return mManager;
    }

    private int mSurfaceWidth, mSurfaceHeight;


    private IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceCreated(@NonNull SurfaceTexture surfaceTexture, int width, int height) {

            LogUtil.v(TAG, "onSurfaceCreated: " + surfaceTexture + " ,current thumbnail: " + mBitmap);

            if (mCurrentPlayerProxy != null)
                bindSurfaceHolder(mCurrentPlayerProxy.getPlayer(), surfaceTexture);
            if (mBitmap != null && !isPlaying()) {
                mRenderThread.render(mBitmap, true, surfaceTexture);
            }

        }


        @Override
        public void onSurfaceChanged(@NonNull SurfaceTexture surfaceTexture, int format, int width, int height) {
            mSurfaceWidth = width;
            mSurfaceHeight = height;
        }

        @Override
        public void onSurfaceDestroyed(@NonNull SurfaceTexture surfaceTexture) {
            LogUtil.v(TAG, "onSurfaceDestroyed: " + surfaceTexture + " ,current thumbnail: " + mBitmap);
        }
    };
    private Bitmap mBitmap;

    private void releaseRenderView() {
        if (mRenderView == null) return;
        if (mCurrentPlayerProxy != null) {
            // Clear display
            mCurrentPlayerProxy.setCurrentDisplay(null);
        }
        LogUtil.v(TAG, "release current renderView: " + mRenderView +
                "\ncurrent bitmap " + mBitmap);
        mRenderView = null;
    }

    private void bindSurfaceHolder(IMediaPlayer mp, SurfaceTexture surfaceTexture) {
        if (mp == null) return;
        if (surfaceTexture == null) {
            mp.setDisplay(null);
            return;
        }
        mp.setSurface(new Surface(surfaceTexture));
    }

    public void configureRenderView(TextureRenderView renderView) {
        if (renderView == null) throw new IllegalArgumentException("Render view can't be null");
        LogUtil.d(TAG, "configure renderView: " + renderView);
        releaseRenderView();
        mRenderView = new WeakReference<>(renderView);
        if (getCurrentVideoWidth() > 0 && getCurrentVideoHeight() > 0) {
            // this will call if the current activity onDestroyed called, then we resume the activity
            // we will check video specs, if we have, then restore them.
            mRenderView.get().setVideoSize(getCurrentVideoWidth(), getCurrentVideoHeight());
        }
        int currentAspectRatio = IRenderView.AR_ASPECT_FILL_PARENT;
        mRenderView.get().setAspectRatioMode(currentAspectRatio);
        mRenderView.get().setRenderCallback(mSHCallback);
    }

    @Override
    public void onPrepared(int videoWidth, int videoHeight, int videoSarNum, int videoSarDen) {
        mRenderView.get().setVideoSize(videoWidth, videoHeight);
        mRenderView.get().setVideoSampleAspectRatio(videoSarNum, videoSarDen);

        if (!mRenderView.get().shouldWaitForResize() || mSurfaceWidth == videoWidth
                || mSurfaceHeight == videoHeight) {
            LogUtil.d(TAG, "onPrepared: bind start");
            mCurrentPlayerProxy.start();
            if (!mCurrentPlayerProxy.isPlaying() && (mCurrentPlayerProxy.getCurrentPosition() > 0)) {
                if (mStateChangeListener != null) mStateChangeListener.onPrepared();
            }
        }
    }

    @Override
    public void onVideoSizeChanged(int videoWidth, int videoHeight, int videoSarNum, int videoSarDen) {
        mRenderView.get().setVideoSize(videoWidth, videoHeight);
        mRenderView.get().setVideoSampleAspectRatio(videoSarNum, videoSarDen);
    }

    public void onPause() {
        pause();
    }

    public void onResume(TextureRenderView renderView) {
        LogUtil.v(TAG, "onResume: current view " + Integer.toHexString(System.identityHashCode(mRenderView.get()))
                + ", target view: " + Integer.toHexString(System.identityHashCode(renderView)));
        if (mRenderView.get() == renderView) return;
        configureRenderView(renderView);
        // buggy if we don't set url immediately in onCreate
//        if (mCurrentPlayerProxy != null)
//            mCurrentPlayerProxy.start();
    }

    /**
     * release specific player playing url
     *
     * @param url the target url
     */
    public void onDestroy(String url) {
        if (mBitmap != null)
            mBitmap.recycle();
        mBitmap = null;
        mRenderView.clear();
        mRenderView = null;
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
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        mBitmap = mRenderView.get().getBitmap();
        LogUtil.v(TAG, "paused, cache thumbnail " + mBitmap + " from " + mRenderView);
        if (mCurrentPlayerProxy != null)
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
        LogUtil.v(TAG, "current map" + mPlayerMap);
        if (mPlayerMap.containsKey(videoUrl)) {
            mCurrentPlayerProxy = mPlayerMap.get(videoUrl);
            LogUtil.v(TAG, "get player from map");
        } else {
            mCurrentPlayerProxy = new ParsingPlayerProxy(mContext.get(), this);
            LogUtil.v(TAG, "create new proxy " + mCurrentPlayerProxy);
            mPlayerMap.put(videoUrl, mCurrentPlayerProxy);
        }
        mCurrentPlayerProxy.play(videoUrl);
    }

    @VisibleForTesting
    void play(VideoInfo info) {
        quickCheckInMap(info.getTitle());
        mCurrentPlayerProxy.setConcatVideos(info);
    }

    @VisibleForTesting
    void playOrigin(String uri) {
        quickCheckInMap(uri);
        mCurrentPlayerProxy.setVideoPath(uri);
    }

    private void quickCheckInMap(String videoUrl) {
        if (mPlayerMap.containsKey(videoUrl)) {
            mCurrentPlayerProxy = mPlayerMap.get(videoUrl);
            LogUtil.v(TAG, "get player from map");
        } else {
            mCurrentPlayerProxy = new ParsingPlayerProxy(mContext.get(), this);
            LogUtil.v(TAG, "create new proxy " + mCurrentPlayerProxy);
            mPlayerMap.put(videoUrl, mCurrentPlayerProxy);
        }

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
     * @param bitmap the bitmap you want to render on view
     */
    public void setQuality(@Quality int quality, Bitmap bitmap) {
        mBitmap = bitmap;
        mCurrentPlayerProxy.setQuality(quality);
    }


    private void destroyPlayerByURL(String url) {
        LogUtil.w(TAG, "destroy url " + url);
        if (mPlayerMap.containsKey(url)) {
            ParsingPlayerProxy player = mPlayerMap.get(url);
            player.release();
            LogUtil.d(TAG, "release player " + player);
            mCurrentPlayerProxy = null;
            mPlayerMap.remove(url);
        } else
            throw new IllegalArgumentException("no player matches this url ");
    }

    public double getCurrentBrightness() {
        return mCurrentPlayerProxy == null ? ((Activity) mContext.get()).getWindow().getAttributes().screenBrightness
                : mCurrentPlayerProxy.getBrightness();
    }

    public void setCurrentBrightness(@FloatRange(from = 0f, to = 1f) double brightness) {
        mCurrentPlayerProxy.setBrightness(brightness);
    }

    public VideoInfo getVideoInfo() {
        return mCurrentPlayerProxy.getVideoInfo();
    }

    public
    @Quality
    int getQuality() {
        return mCurrentPlayerProxy.getQuality();
    }

    public boolean isPreparing() {
        return mCurrentPlayerProxy == null;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mRenderThread.quitSafely();
    }
}
