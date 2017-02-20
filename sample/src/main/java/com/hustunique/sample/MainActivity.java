

package com.hustunique.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hustunique.parsingplayer.player.media.ParsingMediaManager;
import com.hustunique.parsingplayer.player.view.ParsingVideoView;

public class MainActivity extends AppCompatActivity {
    private ParsingVideoView mVideoView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = (ParsingVideoView) findViewById(R.id.videoView);
        mVideoView.play("http://v.youku.com/v_show/id_XMTI2OTAyNzMzNg==.html");
    }

    // turn black when resume to this activity
    @Override
    protected void onResume() {
        super.onResume();
        ParsingMediaManager.getInstance(this).onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
//        ParsingMediaManager.getInstance(this).onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
//        ParsingMediaManager.getInstance(this).onStop();
    }
}
