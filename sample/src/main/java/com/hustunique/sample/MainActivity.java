

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
        mVideoView = (ParsingVideoView) findViewById(R.id.videoView);
        mVideoView.play("http://v.youku.com/v_show/id_XMjQ3MzE1NDA3Ng");
    }
}
