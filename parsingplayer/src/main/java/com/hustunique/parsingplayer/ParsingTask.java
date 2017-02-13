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

package com.hustunique.parsingplayer;

import android.os.AsyncTask;

import com.hustunique.parsingplayer.parser.VideoParser;
import com.hustunique.parsingplayer.parser.entity.VideoInfo;
import com.hustunique.parsingplayer.parser.provider.Quality;
import com.hustunique.parsingplayer.player.ParsingVideoView;

/**
 * Created by CoXier on 17-2-7.
 */

public class ParsingTask extends AsyncTask<String, Void, VideoInfo> {
    private ParsingVideoView mVideoView;
    private int mQuality;

    public ParsingTask(ParsingVideoView videoView, @Quality int quality) {
        mVideoView = videoView;
        mQuality = quality;
    }

    @Override
    protected VideoInfo doInBackground(String... strings) {
        VideoParser videoParser = VideoParser.getInstance();
        return videoParser.parse(strings[0]);
    }

    @Override
    protected void onPostExecute(VideoInfo videoInfo) {
        super.onPostExecute(videoInfo);
        // videoView will start playing automatically when process prepared
        mVideoView.setConcatVideos(videoInfo, mQuality);


    }
}
