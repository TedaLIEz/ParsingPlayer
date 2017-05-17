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

package com.hustunique.parsingplayer.parser.provider;

import com.hustunique.parsingplayer.parser.entity.IVideoInfo;
import com.hustunique.parsingplayer.parser.entity.Quality;

/**
 * Created by JianGuo on 5/17/17.
 */

public class VideoProvider extends IVideoInfoProvider {

    private int mQuality;

    public VideoProvider(IVideoInfo videoInfo, Callback callback) {
        super(videoInfo, callback);
    }


    @Override
    public void provideSource(@Quality int quality) {
        mQuality = quality;
        mCallback.onProvided(mVideoInfo.provideSource(quality));
    }

    @Override
    public int getQuality() {
        return mQuality;
    }
}
