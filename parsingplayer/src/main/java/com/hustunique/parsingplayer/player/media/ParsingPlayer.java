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

package com.hustunique.parsingplayer.player.media;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.hustunique.parsingplayer.BuildConfig;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfo;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * Created by JianGuo on 2/2/17.
 * Wrapper class for {@link IjkMediaPlayer} in singleton, add support for concat protocol file
 */

public class ParsingPlayer implements IParsingPlayer {
    private static final String TAG = "ParsingPlayer";
    private final IjkMediaPlayer mMediaPlayer;

    public ParsingPlayer() {
        this(new Config());
    }

    public ParsingPlayer(Config config) {
        mMediaPlayer = createPlayer(config);
    }


    public void setOption(@OptionCategory int category, String name, String value) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOption(category, name, value);
        }
    }

    public void setOption(@OptionCategory int category, String name, long value) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setOption(category, name, value);
        }
    }


    private IjkMediaPlayer createPlayer(Config config) {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        IjkMediaPlayer.native_setLogLevel(BuildConfig.DEBUG ? IjkMediaPlayer.IJK_LOG_DEFAULT : IjkMediaPlayer.IJK_LOG_ERROR);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,
                "safe", config.safe ? 1 : 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,
                "protocol_whitelist", config.whiteList);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "mediacodec-auto-rotate", config.mediacodecAutoRotate ? 1 : 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "mediacodec-handle-resolution-change", config.mediacodecHandleResolutionChange ? 1 : 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "opensles", config.opensles ? 1 : 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "overlay-format", config.overLayFormat);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "framedrop", config.frameDrop ? 1 : 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "start-on-prepared", config.startOnPrepared ? 1 : 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT,
                "http-detect-range-support", config.httpRangeSupport ? 1 : 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC,
                "skip_loop_filter", config.skipLoopFilter);
        return ijkMediaPlayer;
    }


//    @Override
//    public void setConcatVideoPath(String concatVideoPath, String content, LoadingCallback<String> callback) {
//        mManager.write(concatVideoPath, content, callback);
//    }


    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        mMediaPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaPlayer.setDataSource(context, uri);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> map) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaPlayer.setDataSource(context, uri, map);
    }

    @Override
    public void setDataSource(FileDescriptor fileDescriptor) {
        try {
            mMediaPlayer.setDataSource(fileDescriptor);
        } catch (IOException e) {
            Log.wtf(TAG, e);
        }
    }

    @Override
    public void setDataSource(String s) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        mMediaPlayer.setDataSource(s);
    }

    @Override
    public String getDataSource() {
        return mMediaPlayer.getDataSource();
    }

    @Override
    public void prepareAsync() throws IllegalStateException {
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void start() throws IllegalStateException {
        mMediaPlayer.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        mMediaPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        mMediaPlayer.pause();
    }

    @Override
    public void setScreenOnWhilePlaying(boolean b) {
        mMediaPlayer.setScreenOnWhilePlaying(b);
    }

    @Override
    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long l) throws IllegalStateException {
        mMediaPlayer.seekTo(l);
    }

    @Override
    public long getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public void release() {
        mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
    }

    @Override
    public void setVolume(float v, float v1) {
        mMediaPlayer.setVolume(v, v1);
    }

    @Override
    public int getAudioSessionId() {
        return mMediaPlayer.getAudioSessionId();
    }

    @Override
    public MediaInfo getMediaInfo() {
        return mMediaPlayer.getMediaInfo();
    }

    @Override
    public void setLogEnabled(boolean b) {
        mMediaPlayer.setLogEnabled(b);
    }

    @Override
    public boolean isPlayable() {
        return mMediaPlayer.isPlayable();
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        mMediaPlayer.setOnPreparedListener(onPreparedListener);
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        mMediaPlayer.setOnCompletionListener(onCompletionListener);
    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener) {
        mMediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener) {
        mMediaPlayer.setOnSeekCompleteListener(onSeekCompleteListener);
    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {
        mMediaPlayer.setOnVideoSizeChangedListener(onVideoSizeChangedListener);
    }

    @Override
    public void setOnErrorListener(OnErrorListener onErrorListener) {
        mMediaPlayer.setOnErrorListener(onErrorListener);
    }

    @Override
    public void setOnInfoListener(OnInfoListener onInfoListener) {
        mMediaPlayer.setOnInfoListener(onInfoListener);
    }

    @Override
    public void setOnTimedTextListener(OnTimedTextListener onTimedTextListener) {
        mMediaPlayer.setOnTimedTextListener(onTimedTextListener);
    }

    @Override
    public void setAudioStreamType(int i) {
        mMediaPlayer.setAudioStreamType(i);
    }

    @Override
    public void setKeepInBackground(boolean b) {
        mMediaPlayer.setKeepInBackground(b);
    }

    @Override
    public int getVideoSarNum() {
        return mMediaPlayer.getVideoSarNum();
    }

    @Override
    public int getVideoSarDen() {
        return mMediaPlayer.getVideoSarDen();
    }

    @Override
    public void setWakeMode(Context context, int i) {
        mMediaPlayer.setWakeMode(context, i);
    }

    @Override
    public void setLooping(boolean b) {
        mMediaPlayer.setLooping(b);
    }

    @Override
    public boolean isLooping() {
        return mMediaPlayer.isLooping();
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return mMediaPlayer.getTrackInfo();
    }

    @Override
    public void setSurface(Surface surface) {
        mMediaPlayer.setSurface(surface);
    }

    @Override
    public void setDataSource(IMediaDataSource iMediaDataSource) {
        mMediaPlayer.setDataSource(iMediaDataSource);
    }

    // Though it is not recommended in EJ
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        IjkMediaPlayer.native_profileEnd();
    }

    public static class Config {
        private boolean safe = false;
        private boolean startOnPrepared = true;


        private String whiteList = "rtmp,concat,ffconcat,file,subfile,http,https,tls,rtp,tcp,udp,crypto";
        private boolean mediacodec = true;
        private boolean mediacodecAutoRotate = true;
        private boolean mediacodecHandleResolutionChange = true;
        private boolean opensles = true;
        private int overLayFormat = IjkMediaPlayer.SDL_FCC_RV32;
        private boolean frameDrop = true;
        private boolean httpRangeSupport = false;
        private int skipLoopFilter = 48;

        @Retention(RetentionPolicy.SOURCE)
        @IntDef({IjkMediaPlayer.SDL_FCC_RV16, IjkMediaPlayer.SDL_FCC_RV32,
                IjkMediaPlayer.SDL_FCC_YV12})
        @interface OverlayFormat {
        }

        public void setSafe(boolean safe) {
            this.safe = safe;
        }

        public void setWhiteList(String whiteList) {
            this.whiteList = whiteList;
        }

        public void setMediacodec(boolean mediacodec) {
            this.mediacodec = mediacodec;
        }

        public void setMediacodecAutoRotate(boolean mediacodecAutoRotate) {
            this.mediacodecAutoRotate = mediacodecAutoRotate;
        }

        public void setMediacodecHandleResolutionChange(boolean mediacodecHandleResolutionChange) {
            this.mediacodecHandleResolutionChange = mediacodecHandleResolutionChange;
        }

        public void setOpensles(boolean opensles) {
            this.opensles = opensles;
        }

        public void setOverLayFormat(@OverlayFormat int overLayFormat) {
            this.overLayFormat = overLayFormat;
        }

        public void setFrameDrop(boolean frameDrop) {
            this.frameDrop = frameDrop;
        }

        public void setHttpRangeSupport(boolean httpRangeSupport) {
            this.httpRangeSupport = httpRangeSupport;
        }

        public void setSkipLoopFilter(int skipLoopFilter) {
            this.skipLoopFilter = skipLoopFilter;
        }


        public void setStartOnPrepared(boolean startOnPrepared) {
            this.startOnPrepared = startOnPrepared;
        }
    }


}
