package com.hustunique.parsingplayer.player.media;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.hustunique.parsingplayer.player.io.LoadingCallback;
import com.hustunique.parsingplayer.player.view.TextureRenderView;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.BuildConfig;
import tv.danmaku.ijk.media.player.MediaInfo;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * Created by CoXier on 17-2-19.
 */

public class ParsingMedia {

    private IParsingPlayer mPlayer;
    private TextureRenderView mRenderView;

    private static ParsingMedia mManager;

    private ParsingMedia(Context context){
        mPlayer = createPlayer(context);
    }

    public static ParsingMedia getInstance(Context context){
        if (mManager == null)
            mManager = new ParsingMedia(context);
        return mManager;
    }

    public void configurePlayer(IParsingPlayer player){
        mPlayer = player;
    }

    public void configureRenderView(TextureRenderView renderView){
        mRenderView = renderView;
    }

    public IParsingPlayer getPlayer(){
        return mPlayer;
    }

    public TextureRenderView getRenderView(){
        return mRenderView;
    }

    private IParsingPlayer createPlayer(Context context) {
        IParsingPlayer iParsingPlayer = new ParsingPlayer(context);
        iParsingPlayer.setOnPreparedListener(mPreparedListener);
        iParsingPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
        iParsingPlayer.setOnCompletionListener(mCompletionListener);
        iParsingPlayer.setOnErrorListener(mErrorListener);
        iParsingPlayer.setOnInfoListener(mInfoListener);
        iParsingPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
        iParsingPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
        return iParsingPlayer;
    }

}
