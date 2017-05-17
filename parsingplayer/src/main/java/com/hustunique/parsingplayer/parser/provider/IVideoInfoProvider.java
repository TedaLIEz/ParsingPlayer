package com.hustunique.parsingplayer.parser.provider;

import com.hustunique.parsingplayer.parser.entity.IVideoInfo;

/**
 * Created by JianGuo on 2/12/17.
 */

public abstract class IVideoInfoProvider implements VideoSourceProvider {
    final IVideoInfo mVideoInfo;
    final Callback mCallback;
    public interface Callback {
        void onProvided(String uri);
        void onFail(Exception e);
    }

    IVideoInfoProvider(IVideoInfo videoInfo, Callback callback) {
        mVideoInfo = videoInfo;
        mCallback = callback;
    }

    public IVideoInfo getVideoInfo(){
        return mVideoInfo;
    }
}
