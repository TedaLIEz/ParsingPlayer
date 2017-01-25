

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
        videoParser.parse("http://v.youku.com/v_show/id_XMTI4NjU1NDg4NA==.html", null);

        videoView.setVideoPath("http://k.youku.com/player/getFlvPath/sid/04851694197901206eabe_00/st/mp4/fileid/03002001004F0C50918C8401A9D5F42F647752-A118-3CF8-C56A-C392EF95399E?ypp=0&myp=0&K=b667e7e6245b23cf261f798f%26sign%3D0bb0bb04126cf170a2e0366091803067&ctype=12&token=0504&ev=1&ep=ciacH0iLX8oB7CTWiD8bZyq3JSVaXP4J9h%2BHgdJjALsgPujMnD7YxpXDRvtCF4gQBidwFOmHrdTgbUUWYYFCqGAQ3D3eMfqT%2B%2FaS5d5QzZJ1EBAzccTewVSeRjD1&hd=1&oip=1001157218");
        videoView.start();
    }
}
