

package com.hustunique.jianguo.parsingplayer.parser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hustunique.jianguo.parsingplayer.LogUtil;
import com.hustunique.jianguo.parsingplayer.parser.entity.VideoInfo;
import com.hustunique.jianguo.parsingplayer.parser.extractor.IExtractor;
import com.hustunique.jianguo.parsingplayer.parser.extractor.YoukuExtractor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JianGuo on 1/16/17.
 * Parser extracting video info from a given string.
 */

public class VideoParser {
    private static final String TAG = "VideoParser";
    private IExtractor iExtractor;
    private static Map<String, Class<? extends IExtractor>> sMatchMap = new HashMap<>();

    static {
        // TODO: 1/17/17 Maybe there is a better solution to register map between regex and IExtractor here
        sMatchMap.put(YoukuExtractor.VALID_URL, YoukuExtractor.class);
    }

    @NonNull
    IExtractor createExtractor(@NonNull String url) {
        if (url == null) throw new IllegalArgumentException("Url shouldn't be null");
        Class<? extends IExtractor> clz = findClass(url);
        if (clz != null) {
            try {
                return clz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LogUtil.wtf(TAG, e);
            }
        }
        throw new IllegalArgumentException("This url is not valid or unsupported yet");
    }

    @Nullable
    private Class<? extends IExtractor> findClass(@NonNull String url) {
        for (String reg : sMatchMap.keySet()) {
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return sMatchMap.get(reg);
            }
        }
        return null;
    }

    public VideoInfo parse(String url) {
        iExtractor = createExtractor(url);
        return iExtractor.extract(url);
    }
}
