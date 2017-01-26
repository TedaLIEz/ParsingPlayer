

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
        mVideoView.setVideoPath("http://k.youku.com/player/getFlvPath/sid/0485410264192129d5c64_00/st/mp4/fileid/03002001004F0C50918C8401A9D5F42F647752-A118-3CF8-C56A-C392EF95399E?ypp=0&myp=0&K=5b6b2021d7d8d8c5282c1269%26sign%3Dfc28d62207dea5d1aa5962919aaccbbe&ctype=12&token=0514&ev=1&ep=ciacH02MVswG4SLWij8bbnjnJ3ELXP4J9h%2BHgdJjALsgPujMnD7YxpXDRvtCF4gQBidwFOmHrdTgbUUWYYFCqGAQ3D3eMfqT%2B%2FaS5d5QzZJ1EBAzccTewVSeRjH1&hd=1&oip=1001157218");
        mVideoView.start();
    }

}
