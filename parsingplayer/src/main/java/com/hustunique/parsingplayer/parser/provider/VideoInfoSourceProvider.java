package com.hustunique.parsingplayer.parser.provider;

import com.hustunique.parsingplayer.parser.entity.IVideoInfo;

/**
 * Created by JianGuo on 2/12/17.
 */

public abstract class VideoInfoSourceProvider implements VideoSourceProvider {
    IVideoInfo mVideoInfo;

    VideoInfoSourceProvider(IVideoInfo videoInfo) {
        mVideoInfo = videoInfo;
    }

    public IVideoInfo getVideoInfo(){
        return mVideoInfo;
    }
}
