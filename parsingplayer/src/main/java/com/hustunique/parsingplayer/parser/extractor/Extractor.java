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
import com.google.gson.stream.JsonReader;
import com.hustunique.parsingplayer.parser.ExtractException;
import com.hustunique.parsingplayer.parser.entity.IVideoInfo;
import com.hustunique.parsingplayer.parser.entity.Stream;
import com.hustunique.parsingplayer.parser.entity.VideoInfoImpl;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
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
    OkHttpClient mClient;
    protected String mUrl;

    Extractor() {
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


    public IVideoInfo extract(@NonNull String url) {
        mUrl = url;
        String baseUrl = constructBasicUrl(url);
        final Request request = buildRequest(baseUrl);
        return extract(request);
    }

    @VisibleForTesting
    private IVideoInfo extract(@NonNull Request request) {
        try {
            Response response = mClient.newCall(request).execute();
            VideoInfoImpl videoInfoImpl = createInfo(response);
            return cutDownVideoInfo(videoInfoImpl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ExtractException("Can't extract video info");
    }

    private VideoInfoImpl cutDownVideoInfo(VideoInfoImpl videoInfoImpl) {
        Map<Integer, Stream> streamMap = videoInfoImpl.getStreamMap();
        if (streamMap.keySet().size() <= 4) return videoInfoImpl;
        Map<Integer, Stream> storedStreamMap = new HashMap<>();
        Object[] keys = streamMap.keySet().toArray();
        for (int i = 3; i >= 0; i--) {
            storedStreamMap.put(i, streamMap.get(keys[keys.length - 4 + i]));
        }
        return new VideoInfoImpl(mUrl, storedStreamMap, videoInfoImpl.getTitle(), videoInfoImpl.getId());
    }

    protected JsonObject parseResponse(String response) {
        JsonReader reader = new JsonReader(new StringReader(response));
        reader.setLenient(true);
        JsonParser parser = new JsonParser();
        return parser.parse(reader).getAsJsonObject();
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

    /**
     * @param height the height of video in pixel
     * @return {@link IVideoInfo#HD_LOW}
     * {@link IVideoInfo#HD_MEDIUM}
     * {@link IVideoInfo#HD_STANDARD}
     * {@link IVideoInfo#HD_HIGH}
     */
    protected int calQualityByHeight(int height) {
        if (height <= 0) return IVideoInfo.HD_UNSPECIFIED;
        if (height <= 320) {
            return IVideoInfo.HD_LOW;
        } else if (height <= 480) {
            return IVideoInfo.HD_MEDIUM;
        } else if (height <= 720) {
            return IVideoInfo.HD_STANDARD;
        } else {
            return IVideoInfo.HD_HIGH;
        }
    }


    abstract String constructBasicUrl(@NonNull String url);

    @Nullable
    abstract VideoInfoImpl createInfo(@NonNull Response response) throws IOException;

    @NonNull
    abstract Request buildRequest(@NonNull String baseUrl);
}
