

package com.hustunique.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.hustunique.parsingplayer.player.view.ParsingVideoView;

public class MainActivity extends AppCompatActivity {
    private ParsingVideoView mVideoView;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = (ParsingVideoView) findViewById(R.id.videoView);
        final Button button = (Button) findViewById(R.id.play);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.play("http://v.youku.com/v_show/id_XOTY1MDAyNDY4.html");
            }
        });

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
        mVideoView.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.onDestroy();
    }
}
