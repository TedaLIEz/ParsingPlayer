

package com.hustunique.jianguo.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hustunique.jianguo.parsingplayer.parser.VideoParser;
import com.hustunique.jianguo.parsingplayer.player.ParsingVideoView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VideoParser videoParser = new VideoParser();
        ParsingVideoView videoView = new ParsingVideoView(this);
        RelativeLayout.LayoutParams layoutParams
                = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(layoutParams);
        ((RelativeLayout) findViewById(R.id.activity_main)).addView(videoView);
        videoParser.parse("http://v.youku.com/v_show/id_XMTc1ODE5Njcy.html", null);

        videoView.setVideoPath("http://k.youku.com/player/getFlvPath/sid/048532424789612650177_00/st/flv/fileid/03008002015884C218BA2F2D9B7D2FE635B72E-A1E7-8E04-2D36-880884D5A14D?ypp=0&myp=0&K=eb4be7ee804c38de2412e30a%26sign%3Dbb61f488d675ab5b2fd625336ee5597d&ctype=12&token=0534&ev=1&ep=ciacH0qPUswE4ivWjj8bYSnidXAIXP4J9h%2BNgdJgALohQOC76jzQz%2B%2FBTIlAYvBrdVYEZp73qNWVbUJhYYFC3G8Q1zuoPfriivPl5aVbxJgIYm0zA8zTwFSeRjP1&hd=1&oip=1001157218");
        videoView.start();
    }
}
