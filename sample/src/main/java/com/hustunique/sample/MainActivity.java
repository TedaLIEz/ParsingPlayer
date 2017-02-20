

package com.hustunique.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hustunique.parsingplayer.player.view.ParsingVideoView;
import com.hustunique.parsingplayer.util.LogUtil;

public class MainActivity extends AppCompatActivity {
    private ParsingVideoView mVideoView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = (ParsingVideoView) findViewById(R.id.videoView);
        mVideoView.play("http://v.youku.com/v_show/id_XMTI2OTAyNzMzNg==.html");
        LogUtil.i("ParsingVideoView", "onCreate, width " + mVideoView.getWidth() + ", height " + mVideoView.getHeight());
    }

    // turn black when resume to this activity
    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mVideoView.onStop();
    }
}
