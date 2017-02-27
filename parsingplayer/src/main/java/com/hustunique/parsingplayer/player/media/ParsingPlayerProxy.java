/*
 * Copyright (c) 2017 UniqueStudio
 *
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

package com.hustunique.parsingplayer.player.media;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.SurfaceHolder;

import com.hustunique.parsingplayer.parser.entity.VideoInfo;
import com.hustunique.parsingplayer.parser.provider.ConcatSourceProvider;
import com.hustunique.parsingplayer.parser.provider.Quality;
import com.hustunique.parsingplayer.parser.provider.VideoInfoSourceProvider;
import com.hustunique.parsingplayer.player.io.LoadingCallback;
import com.hustunique.parsingplayer.player.view.IMediaPlayerControl;
import com.hustunique.parsingplayer.util.LogUtil;

import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by JianGuo on 2/19/17.
 * Proxy class for {@link IParsingPlayer} instance, we bind one uri to one instance of
 * {@link IParsingPlayer} in a hashMap
 */

class ParsingPlayerProxy implements IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayerControl {
    private static final String TAG = "ParsingPlayerProxy";
    private final Context mContext;
    private int mCurrentState;

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private IParsingPlayer mPlayer;
    private ParsingTask mParsingTask;
    private VideoInfoSourceProvider mProvider;
    private int mBufferPercentage;
    private double mBrightness = Double.MAX_VALUE;

    private int mVideoWidth, mVideoHeight;
    private int mVideoSarNum, mVideoSarDen;
    private int mSeekWhenPrepared;

    ParsingPlayerProxy(Context context, OnStateListener listener) {
        mOnVideoPreparedListener = listener;
        mContext = context;
    }


    private IParsingPlayer createPlayer(Context context) {
        mCurrentState = STATE_IDLE;
        IParsingPlayer iParsingPlayer = new ParsingPlayer(context);
        AudioManager audioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
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


    private OnStateListener mOnVideoPreparedListener;

    IMediaPlayer getPlayer() {
        return mPlayer;
    }

    int getVideoWidth() {
        return mVideoWidth;
    }

    int getVideoHeight() {
        return mVideoHeight;
    }

    int getVideoSarDen() {
        return mVideoSarDen;
    }

    int getVideoSarNum() {
        return mVideoSarNum;
    }


    void setBrightness(@FloatRange(from = 0f, to = 1f) double brightness) {
        mBrightness = brightness;
    }

    double getBrightness() {
        return mBrightness;
    }

    /**
     * Release current used player
     */
    private void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
            mCurrentState = STATE_IDLE;
            AudioManager am = (AudioManager) mContext.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    void setCurrentDisplay(SurfaceHolder holder) {
        if (mPlayer != null)
            mPlayer.setDisplay(holder);
    }

    void release() {
        releasePlayer();
    }

    VideoInfo getVideoInfo() {
        if (mProvider != null)
            return mProvider.getVideoInfo();
        return null;
    }

    public @Quality int getQuality() {
        return mProvider.getQuality();
    }

    public void setQuality(@Quality int quality) {
        mSeekWhenPrepared = (int) mPlayer.getCurrentPosition();
        mPlayer.setDisplay(null);
        mPlayer.release();
        mPlayer = createPlayer(mContext);
        setConcatVideos(quality);
    }


    interface OnStateListener {
        void onPrepared(int videoWidth, int videoHeight, int videoSarNum, int videoSarDen);

        void onVideoSizeChanged(int videoWidth, int videoHeight, int videoSarNum, int videoSarDen);

        void onCompleted();

        void onError(String msg);

        void onInfo(int arg);
    }

    void setStateListener(@Nullable OnStateListener onVideoPreparedListener) {
        mOnVideoPreparedListener = onVideoPreparedListener;
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        LogUtil.i(TAG,"Proxy onPrepared");
        mCurrentState = STATE_PLAYING;
        mVideoWidth = mp.getVideoWidth();
        mVideoHeight = mp.getVideoHeight();
        int seekToPos = mSeekWhenPrepared;
        if (seekToPos != 0) {
            seekTo(seekToPos);
        }
        if (mVideoHeight != 0 && mVideoWidth != 0) {
            if (mOnVideoPreparedListener != null)
                mOnVideoPreparedListener.onPrepared(mVideoWidth,
                        mVideoHeight, mVideoSarNum, mVideoSarDen);
        }
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
        mVideoHeight = mp.getVideoHeight();
        mVideoWidth = mp.getVideoWidth();
        mVideoSarNum = mp.getVideoSarNum();
        mVideoSarDen = mp.getVideoSarDen();
        if (mVideoWidth != 0 && mVideoHeight != 0) {
            if (mOnVideoPreparedListener != null)
                mOnVideoPreparedListener.onVideoSizeChanged(mVideoWidth, mVideoHeight,
                        mVideoSarNum, mVideoSarDen);
        }
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        mCurrentState = STATE_PLAYBACK_COMPLETED;
        if (mOnVideoPreparedListener != null)
            mOnVideoPreparedListener.onCompleted();
    }

    @Override
    public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
        LogUtil.e(TAG, "Error: " + framework_err + "," + impl_err);
        mCurrentState = STATE_ERROR;
        if (mOnVideoPreparedListener != null)
            // TODO: 2/19/17 Convert identifier to msg
            mOnVideoPreparedListener.onError("Some Error");
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
        if (mOnVideoPreparedListener != null)
            mOnVideoPreparedListener.onInfo(arg1);
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
                break;
            case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                LogUtil.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                break;
        }
        return true;
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {

    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        mBufferPercentage = percent;
    }

    @Override
    public void start() {
        if (mSeekWhenPrepared != 0) {
            seekTo(mSeekWhenPrepared);
        }
        if (isInPlayBackState()) {
            mPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
    }

    @Override
    public void pause() {
        if (isInPlayBackState()) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
        }
    }

    @Override
    public int getDuration() {
        if (isInPlayBackState()) {
            return (int) mPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlayBackState()) {
            return (int) mPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlayBackState()) {
            mPlayer.seekTo(pos);
            mSeekWhenPrepared = 0;
        } else {
            mSeekWhenPrepared = pos;
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlayBackState() && mPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return mBufferPercentage;
    }

    @Override
    public void play(String videoUrl) {
        if (mPlayer != null && mPlayer.isPlaying()) {
            return;
        }
        mParsingTask = new ParsingTask(this);
        mParsingTask.execute(videoUrl);
    }


    void setConcatVideos(@NonNull VideoInfo videoInfo) {
        if (mPlayer == null)
            mPlayer = createPlayer(mContext);
        mProvider = new ConcatSourceProvider(videoInfo, mContext);
        setConcatContent(mProvider.provideSource(VideoInfo.HD_UNSPECIFIED));
    }

    private void setConcatVideos(@Quality int quality){
        setConcatContent(mProvider.provideSource(quality));
    }
    private void setConcatContent(String content) {
        LogUtil.i(TAG, "set temp file content: \n" + content);
        mPlayer.setConcatVideoPath(SystemClock.currentThreadTimeMillis() + "",
                content, new LoadingCallback<String>() {
                    @Override
                    public void onSuccess(final String result) {
                        // use post here to run in main thread
                        setVideoURI(Uri.parse(result));
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Log.wtf(TAG, e);
                    }
                });
    }

    @VisibleForTesting
    void setVideoPath(String path) {
        if (mPlayer == null)
            mPlayer = createPlayer(mContext);
        try {
            mPlayer.setDataSource(mContext, Uri.parse(path));
            openVideo();
        } catch (IOException e) {
            LogUtil.wtf(TAG, e);
            e.printStackTrace();
        }
    }

    private void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }


    private void setVideoURI(Uri uri, Map<String, String> headers) {
        try {
            mPlayer.setDataSource(mContext, uri, headers);
            openVideo();
        } catch (IOException e) {
            LogUtil.wtf(TAG, e);
            mCurrentState = STATE_ERROR;
        }
    }

    private void openVideo() {
        mCurrentState = STATE_PREPARING;
        mBufferPercentage = 0;
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.prepareAsync();
    }

    boolean isInPlayBackState() {
        return mCurrentState != STATE_ERROR
                && mCurrentState != STATE_IDLE
                && mCurrentState != STATE_PREPARING;
    }



    @Override
    public int getAudioSessionId() {
        return 0;
    }

}
