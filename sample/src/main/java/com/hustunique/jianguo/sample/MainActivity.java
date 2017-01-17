

package com.hustunique.jianguo.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hustunique.jianguo.parsingplayer.parser.VideoParser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoParser videoParser = new VideoParser();
        videoParser.parse("http://v.youku.com/v_show/id_XMTc1ODE5Njcy.html");
    }
}
