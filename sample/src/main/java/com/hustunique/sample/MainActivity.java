

package com.hustunique.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hustunique.parsingplayer.parser.VideoParser;
import com.hustunique.parsingplayer.parser.entity.VideoInfo;
import com.hustunique.parsingplayer.parser.extractor.Extractor;
import com.hustunique.parsingplayer.player.ParsingVideoView;

public class MainActivity extends AppCompatActivity {
    private ParsingVideoView mVideoView;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VideoParser videoParser = new VideoParser();
        mVideoView = (ParsingVideoView) findViewById(R.id.videoView);
        videoParser.parse("http://v.youku.com/v_show/id_XMjQ3MzE1NDA3Ng", new Extractor.ExtractCallback() {
            @Override
            public void onSuccess(VideoInfo videoInfo) {
//                Log.d(TAG, videoInfo.toString());
                mVideoView.setConcatVideos(videoInfo);
                mVideoView.start();
            }

            @Override
            public void onError(Throwable e) {
                Log.wtf(TAG, e);
            }
        });


    }
}
