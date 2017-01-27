

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
        mVideoView.setVideoPath("http://k.youku.com/player/getFlvPath/sid/04854905498521262fdc9_00/st/flv/fileid/03000201004B6DDBD98C8401A9D5F42F647752-A118-3CF8-C56A-C392EF95399E?ypp=0&myp=0&K=9c1f599030999e4a282c1305%26sign%3De56c44d0aabfc3ff6d9958e16c4b82f3&ctype=12&token=0514&ev=1&ep=ciacH02EVssE7Cvaij8bYS60ICQGXP4J9h%2BFg9JjALsgOu7L7UylzpXDRvtCF4gQBidwFOmHrdTgbUUWYYFCqGAQ3D3eMfqT%2B%2FaS5d5QzZJ1EBAzccTewVSeRjH1&hd=0&oip=1001157218");
        mVideoView.start();
    }

}
