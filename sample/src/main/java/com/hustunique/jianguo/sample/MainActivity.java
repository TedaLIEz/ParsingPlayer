

package com.hustunique.jianguo.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hustunique.jianguo.parsingplayer.parser.VideoParser;
import com.hustunique.jianguo.parsingplayer.player.ParsingVideoView;

public class MainActivity extends AppCompatActivity {
    private ParsingVideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VideoParser videoParser = new VideoParser();
        videoParser.parse("http://v.youku.com/v_show/id_XMTc1ODE5Njcy.html", null);
        mVideoView = (ParsingVideoView) findViewById(R.id.videoView);
        mVideoView.setVideoPath("http://k.youku.com/player/getFlvPath/sid/0485329740862125c5f21_00/st/flv/fileid/03008002015884C218BA2F2D9B7D2FE635B72E-A1E7-8E04-2D36-880884D5A14D?ypp=0&myp=0&K=ed6f1a1652c1aab1261f7a6d%26sign%3D2da199687b8b4baf95fa8a3e922dfeed&ctype=12&token=0514&ev=1&ep=ciacH0qPX8kE5SvZij8bYn%2FnInUOXP4J9h%2BNgdJgALohQOC76jzQz%2B%2FBTIlAYvBrdVYEZp73qNWVbUJhYYFC3G8Q1zuoPfriivPl5aVbxJgIYm0zA8zTwFSeRjH1&hd=1&oip=1001157218");
        mVideoView.start();
    }

}
