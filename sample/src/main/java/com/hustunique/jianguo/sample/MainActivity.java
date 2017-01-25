

package com.hustunique.jianguo.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hustunique.jianguo.parsingplayer.parser.VideoParser;
import com.hustunique.jianguo.parsingplayer.parser.entity.VideoInfo;
import com.hustunique.jianguo.parsingplayer.parser.extractor.Extractor;
import com.hustunique.jianguo.parsingplayer.player.ParsingVideoView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        VideoParser videoParser = new VideoParser();
        final ParsingVideoView videoView = new ParsingVideoView(this);
        RelativeLayout.LayoutParams layoutParams
                = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        videoView.setLayoutParams(layoutParams);
        ((RelativeLayout) findViewById(R.id.activity_main)).addView(videoView);
        videoParser.parse("http://v.youku.com/v_show/id_XMTI4NjU1NDg4NA==.html", new Extractor.ExtractCallback() {
            @Override
            public void onSuccess(VideoInfo videoInfo) {
                Log.d(TAG, videoInfo.toString());
                videoView.setVideoPath(videoInfo.getSegs("3gphd").get(0).getPath());
                videoView.start();
            }

            @Override
            public void onError(Throwable e) {
                Log.wtf(TAG, e);
            }
        });

    }
}
