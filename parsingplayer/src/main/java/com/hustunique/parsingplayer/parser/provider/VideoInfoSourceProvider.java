package com.hustunique.parsingplayer.parser.provider;

import com.hustunique.parsingplayer.parser.entity.VideoInfo;

/**
 * Created by JianGuo on 2/12/17.
 */

public abstract class VideoInfoSourceProvider implements VideoSourceProvider {
    protected VideoInfo mVideoInfo;

    public VideoInfoSourceProvider(VideoInfo videoInfo) {
        mVideoInfo = videoInfo;
    }
}
