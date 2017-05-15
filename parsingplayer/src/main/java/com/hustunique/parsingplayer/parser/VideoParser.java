/*
 *
 * Copyright (c) 2017 UniqueStudio
 *
 * This file is part of ParsingPlayer.
 *
 * ParsingPlayer is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ParsingPlayer; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.hustunique.parsingplayer.parser;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hustunique.parsingplayer.parser.entity.IVideoInfo;
import com.hustunique.parsingplayer.parser.extractor.BilibiliExtractor;
import com.hustunique.parsingplayer.parser.extractor.Extractor;
import com.hustunique.parsingplayer.parser.extractor.QQExtractor;
import com.hustunique.parsingplayer.parser.extractor.SoHuExtractor;
import com.hustunique.parsingplayer.parser.extractor.YoukuExtractor;
import com.hustunique.parsingplayer.util.LogUtil;

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
    private Extractor mExtractor;
    private static Map<String, String> sMatchMap = new HashMap<>();

    private static VideoParser mParser;

    private VideoParser() {
    }

    public static VideoParser getInstance(){
        if (mParser == null){
            mParser = new VideoParser();
        }
        return mParser;
    }

    static {
        sMatchMap.put(YoukuExtractor.VALID_URL, "com.hustunique.parsingplayer.parser.extractor.YoukuExtractor");
        sMatchMap.put(SoHuExtractor.VALID_URL,"com.hustunique.parsingplayer.parser.extractor.SoHuExtractor");
        sMatchMap.put(BilibiliExtractor.VALID_URL,"com.hustunique.parsingplayer.parser.extractor.BilibiliExtractor");
        sMatchMap.put(QQExtractor.VALID_URL,"com.hustunique.parsingplayer.parser.extractor.QQExtractor");
    }

    @NonNull
    Extractor createExtractor(@NonNull String url) throws ExtractException{
        if (url == null) throw new ExtractException("Url shouldn't be null");
        Class<? extends Extractor> clz = findClass(url);
        if (clz != null) {
            try {
                return clz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LogUtil.wtf(TAG, e);
            }
        }
        throw new ExtractException("This url is not valid or unsupported yet");
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private Class<? extends Extractor> findClass(@NonNull String url) {
        for (String reg : sMatchMap.keySet()) {
            Pattern pattern = Pattern.compile(reg);
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                try {
                    return (Class<? extends Extractor>) Class.forName(sMatchMap.get(reg));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public IVideoInfo parse(String url) {
        try {
            mExtractor = createExtractor(url);
            return mExtractor.extract(url);
        } catch (ExtractException e) {
            Log.wtf(TAG,e);
        }
        return null;
    }


}
