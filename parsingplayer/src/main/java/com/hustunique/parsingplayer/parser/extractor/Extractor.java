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

package com.hustunique.parsingplayer.parser.extractor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hustunique.parsingplayer.parser.ExtractException;
import com.hustunique.parsingplayer.parser.entity.Seg;
import com.hustunique.parsingplayer.parser.entity.Stream;
import com.hustunique.parsingplayer.parser.entity.VideoInfo;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by JianGuo on 1/16/17.
 * Interface for extracting video info from websites
 */

public abstract class Extractor {
    private static final String TAG = "Extractor";
    protected OkHttpClient mClient;

    public Extractor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        mClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }


    public VideoInfo extract(@NonNull String url) {
        String baseUrl = constructBasicUrl(url);
        final Request request = buildRequest(baseUrl);
        return extract(request);
    }

    @VisibleForTesting
    private VideoInfo extract(@NonNull Request request) {
        try {
            Response response = mClient.newCall(request).execute();
            VideoInfo videoInfo = createInfo(response);
            return cutDownVideoInfo(videoInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ExtractException("Can't extract video info");
    }

    private VideoInfo cutDownVideoInfo(VideoInfo videoInfo) {
        Map<Integer, Stream> streamMap = videoInfo.getStreamMap();
        if (streamMap.keySet().size() <= 4) return videoInfo;
        Map<Integer, Stream> storedStreamMap = new HashMap<>();
        Object[] keys = streamMap.keySet().toArray();
        for (int i = 3; i >= 0; i--) {
            storedStreamMap.put(i, streamMap.get(keys[keys.length - 4 + i]));
        }
        return new VideoInfo(storedStreamMap, videoInfo.getTitle());
    }

    protected JsonObject parseResponse(String response) {
        JsonParser parser = new JsonParser();
        return parser.parse(response).getAsJsonObject();
    }

    protected String searchValue(String s, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        matcher.find();
        return matcher.group(0);
    }

    protected String downloadData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return mClient.newCall(request).execute().body().string();
    }

    protected String downloadData(String url, Map<String, String> headers, Map<String, String> postData) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        for (String headerKey : headers.keySet()) {
            requestBuilder.addHeader(headerKey, headers.get(headerKey));
        }

        FormBody.Builder bodyBuilder = new FormBody.Builder();
        for (String bodyKey : postData.keySet()) {
            bodyBuilder.add(bodyKey, postData.get(bodyKey));
        }
        RequestBody body = bodyBuilder.build();
        Request request = requestBuilder.url(url).post(body).build();
        return mClient.newCall(request).execute().body().string();
    }

    abstract String constructBasicUrl(@NonNull String url);

    @Nullable
    abstract VideoInfo createInfo(@NonNull Response response) throws IOException;

    @NonNull
    abstract Request buildRequest(@NonNull String baseUrl);
}
