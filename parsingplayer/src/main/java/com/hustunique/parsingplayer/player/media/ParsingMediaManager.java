package com.hustunique.parsingplayer.player.media;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.hustunique.parsingplayer.parser.entity.VideoInfo;
import com.hustunique.parsingplayer.parser.provider.ConcatSourceProvider;
import com.hustunique.parsingplayer.parser.provider.Quality;
import com.hustunique.parsingplayer.parser.provider.VideoInfoSourceProvider;
import com.hustunique.parsingplayer.player.io.LoadingCallback;
import com.hustunique.parsingplayer.player.view.IMediaPlayerControl;
import com.hustunique.parsingplayer.player.view.IRenderView;
import com.hustunique.parsingplayer.player.view.TextureRenderView;
import com.hustunique.parsingplayer.util.LogUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by CoXier on 17-2-19.
 */
// TODO: 2/19/17 Refactor on this class
public class ParsingMediaManager implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayerControl {
    private static final String TAG = "ParsingMediaManager";
    private int mCurrentState;
    private int mTargetState;
    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private int mCurrentAspectRatio = IRenderView.AR_ASPECT_FIT_PARENT;
    private int mVideoWidth;
    private int mVideoHeight;

    private int mVideoSarNum;
    private int mVideoSarDen;

    private int mSeekWhenPrepared;


    private Map<String, IParsingPlayer> mPlayerMap;
    private String mCurrentUri;
    private IParsingPlayer mCurrentPlayer;
    private TextureRenderView mRenderView;

    private static ParsingMediaManager mManager;
    private int mCurrentBufferPercentage;
    private ParsingTask mParsingTask;
    private Context mContext;
    private VideoInfoSourceProvider mProvider;

    private ParsingMediaManager(Context context) {
        mPlayerMap = new HashMap<>();
        mContext = context;
    }

    public static ParsingMediaManager getInstance(Context context) {
        if (mManager == null)
            mManager = new ParsingMediaManager(context);
        return mManager;
    }


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
            if (mCurrentPlayer != null) bindSurfaceHolder(mCurrentPlayer, holder);
        }

        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                LogUtil.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            boolean isValidState = (mTargetState == STATE_PLAYING);
            boolean hasValidSize = !mRenderView.shouldWaitForResize()
                    || (mVideoWidth == width && mVideoHeight == height);
            if (mCurrentPlayer != null && isValidState && hasValidSize) {
                if (mSeekWhenPrepared != 0) {
                    seekTo(mSeekWhenPrepared);
                }
                start();
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
        if (mCurrentPlayer != null) {
            mCurrentPlayer.setDisplay(null);
        }
    }


    public void configureRenderView(TextureRenderView renderView) {
        if (mRenderView == renderView) return;
        if (mRenderView != null) {
            if (mCurrentPlayer != null) {
                mCurrentPlayer.setDisplay(null);
            }

            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView = null;
        }
        mRenderView = renderView;
        mRenderView.getSurfaceHolder().bindToMediaPlayer(mCurrentPlayer);
        mRenderView.setAspectRatioMode(mCurrentAspectRatio);
        mRenderView.addRenderCallback(mSHCallback);
    }


    public void setStateListener(StateListener stateListener) {
        mStateListener = stateListener;
    }


    private IParsingPlayer createPlayer(Context context) {
        mCurrentState = mTargetState = STATE_IDLE;
        IParsingPlayer iParsingPlayer = new ParsingPlayer(context);
        AudioManager audioManager = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        iParsingPlayer.setOnPreparedListener(this);
        iParsingPlayer.setOnVideoSizeChangedListener(this);
        iParsingPlayer.setOnCompletionListener(this);
        iParsingPlayer.setOnErrorListener(this);
        iParsingPlayer.setOnInfoListener(this);
        iParsingPlayer.setOnBufferingUpdateListener(this);
        iParsingPlayer.setOnSeekCompleteListener(this);
        return iParsingPlayer;
    }

    private StateListener mStateListener;

    @Override
    public void onPrepared(IMediaPlayer mp) {
        mCurrentState = STATE_PREPARED;
        mTargetState = STATE_PLAYING;
        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();
        int seekToPosition = mSeekWhenPrepared;
        if (seekToPosition != 0) {
            seekTo(seekToPosition);
        }
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            if (mRenderView != null) {
                mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);

                if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth
                        || mSurfaceHeight == mVideoHeight) {
                    if (mTargetState == STATE_PLAYING) {
                        start();
                    } else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
                        // TODO: show controllerView
                        if (mStateListener != null) mStateListener.onPrepared();
                    }
                }
            }
        } else {
            if (mTargetState == STATE_PLAYING) {
                start();
            }
        }
    }


    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();
        mVideoSarNum = mp.getVideoSarNum();
        mVideoSarDen = mp.getVideoSarDen();
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            if (mRenderView != null) {
                mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
            }
        }
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        mTargetState = STATE_PLAYBACK_COMPLETED;
        if (mStateListener != null) mStateListener.onCompleted();
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int framework_err, int impl_err) {
        LogUtil.e(TAG, "Error: " + framework_err + "," + impl_err);
        mCurrentState = STATE_ERROR;
        mTargetState = STATE_ERROR;
        if (mStateListener != null) mStateListener.onError(" some Error");
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int arg1, int arg2) {
        switch (arg1) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                LogUtil.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                LogUtil.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                LogUtil.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                LogUtil.w(TAG, "Bad networking!");
                LogUtil.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                break;
            case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                LogUtil.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                break;
            case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                LogUtil.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                break;
            case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                LogUtil.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                break;
            case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                LogUtil.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                break;
            case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                LogUtil.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                break;
            case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                LogUtil.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                break;
            case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                LogUtil.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
//                if (mRenderView != null)
//                        mRenderView.setVideoRotation(arg2);
                break;
            case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                LogUtil.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                break;
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        mCurrentBufferPercentage = percent;
    }

    @Override
    public void onSeekComplete(IMediaPlayer mp) {

    }

    @Override
    public void start() {
        if (isInPlayBackState()) {
            mCurrentPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
    }

    private boolean isInPlayBackState() {
        return (mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    public void pause() {
        if (isInPlayBackState()) {
            if (mCurrentPlayer.isPlaying()) {
                mCurrentPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
        mTargetState = STATE_PAUSED;
    }

    @Override
    public int getDuration() {
        if (isInPlayBackState()) {
            return (int) mCurrentPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlayBackState()) {
            return (int) mCurrentPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlayBackState()) {
            LogUtil.d(TAG, "seekTo: " + pos);
            mCurrentPlayer.seekTo(pos);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = pos;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlayBackState() && mCurrentPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mCurrentBufferPercentage;
    }


    @Override
    public void play(String videoUrl) {
        if (mCurrentPlayer != null && mCurrentPlayer.isPlaying() && videoUrl.equals(mCurrentUri)) {
            return;
        }
        releaseCurrentPlayer();
        mCurrentUri = videoUrl;
        if (mPlayerMap.containsKey(videoUrl)) {
            mCurrentPlayer = mPlayerMap.get(videoUrl);
        } else {
            mCurrentPlayer = createPlayer(mContext);
            mPlayerMap.put(videoUrl, mCurrentPlayer);
        }

        play(videoUrl, VideoInfo.HD_UNSPECIFIED);
    }


    private void releaseCurrentPlayer() {
        if (mCurrentPlayer != null) {
            pause();
            AudioManager am = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
            mCurrentPlayer.setDisplay(null);
        }
    }


    private void play(String videoUrl, @Quality int quality) {
        mParsingTask = new ParsingTask(this, quality);
        mParsingTask.execute(videoUrl);
    }

    public void setConcatVideos(@NonNull VideoInfo videoInfo, @Quality int quality) {
        mProvider = new ConcatSourceProvider(videoInfo, mContext);
        setConcatContent(mProvider.provideSource(quality));
    }

    // TODO: 2/5/17 Show sth if the io is running
    private void setConcatContent(String content) {
        LogUtil.i(TAG, "set temp file content: \n" + content);
        mCurrentPlayer.setConcatVideoPath(SystemClock.currentThreadTimeMillis() + "",
                content, new LoadingCallback<String>() {
                    @Override
                    public void onSuccess(final String result) {
                        // use post here to run in main thread
                        setVideoPath(result);

                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.wtf(TAG, e);
                    }
                });
    }


    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }


    private void setVideoURI(Uri uri, Map<String, String> headers) {
        try {
            mCurrentPlayer.setDataSource(mContext, uri, headers);
            openVideo();
        } catch (IOException e) {
            LogUtil.wtf(TAG, e);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
        }
    }

    private void openVideo() {
        mCurrentBufferPercentage = 0;
        mCurrentPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        bindSurfaceHolder(mCurrentPlayer, mSurfaceHolder);
        mCurrentPlayer.prepareAsync();
        mCurrentState = STATE_PREPARING;
    }
    @Override
    public int getAudioSessionId() {
        return 0;
    }


}
