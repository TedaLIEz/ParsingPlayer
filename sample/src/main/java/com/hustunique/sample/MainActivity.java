

package com.hustunique.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hustunique.parsingplayer.parser.extractor.BilibiliExtractor;
import com.hustunique.parsingplayer.parser.extractor.SoHuExtractor;
import com.hustunique.parsingplayer.player.ParsingVideoView;

public class MainActivity extends AppCompatActivity {
    private ParsingVideoView mVideoView;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = (ParsingVideoView) findViewById(R.id.videoView);
        mVideoView.play(SoHuExtractor.TEST_URL);
    }
}
