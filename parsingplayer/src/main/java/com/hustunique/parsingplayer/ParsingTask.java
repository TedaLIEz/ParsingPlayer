package com.hustunique.parsingplayer;

import android.os.AsyncTask;

import com.hustunique.parsingplayer.parser.VideoParser;
import com.hustunique.parsingplayer.parser.entity.VideoInfo;
import com.hustunique.parsingplayer.player.ParsingVideoView;

/**
 * Created by CoXier on 17-2-7.
 */

public class ParsingTask extends AsyncTask<String, Void, VideoInfo> {
    private ParsingVideoView mVideoView;

    public ParsingTask(ParsingVideoView videoView) {
        this.mVideoView = videoView;
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
        mVideoView.setConcatVideos(videoInfo);

    }
}
