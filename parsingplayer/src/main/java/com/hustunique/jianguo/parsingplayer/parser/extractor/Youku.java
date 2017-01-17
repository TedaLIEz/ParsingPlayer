package com.hustunique.jianguo.parsingplayer.parser.extractor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hustunique.jianguo.parsingplayer.parser.entity.VideoInfo;

/**
 * Created by CoXier on 17-1-17.
 */

public class Youku implements IExtractor {
    public static final String VALID_URL = "(?:http://(?:v|player)\\.youku\\.com/(?:v_show/id_|player\\.php/sid/)|youku:)([A-Za-z0-9]+)(?:\\.html|/v\\.swf|)";
    public static final String ID_REGEX = "((?<=id_)|(?<=sid/))[A-Za-z0-9]+";

    @Nullable
    @Override
    public VideoInfo extract(@NonNull String url) {
        return null;
    }
}
