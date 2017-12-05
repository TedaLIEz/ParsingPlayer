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
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hustunique.parsingplayer.parser.ExtractException;
import com.hustunique.parsingplayer.parser.entity.Seg;
import com.hustunique.parsingplayer.parser.entity.Stream;
import com.hustunique.parsingplayer.parser.entity.VideoInfoImpl;
import com.hustunique.parsingplayer.util.LogUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CoXier on 17-1-17.
 */

public class YoukuExtractor extends Extractor {
    public static final String VALID_URL = "(?:http://(?:v|player)\\.youku\\.com/(?:v_show/id_|player\\.php/sid/)|youku:)([A-Za-z0-9]+)(?:\\.html|/v\\.swf|)";
    private static final String ID_REGEX = "((?<=id_)|(?<=sid/))[A-Za-z0-9]+";
    private static final String TAG = "YoukuExtractor";
    private static final char[] letterTable = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static final String TEST_URL = "http://v.youku.com/v_show/id_XMjUwODc1MTY5Mg==.html";


    private String mId;


    @Override
    @Nullable
    VideoInfoImpl createInfo(@NonNull Response response) throws IOException {
        JsonObject data = getData(response.body().string());
        if (data == null) return null;
        checkError(data);
        String title = getTitle(data);
        Map<Integer, Stream> streamMap = getSegMap(data);
        LogUtil.d(TAG, "hd length:" + streamMap.keySet().size());
        return new VideoInfoImpl(mUrl, streamMap, title, mId);
    }

    private Map<Integer, Stream> getSegMap(JsonObject data) throws UnsupportedEncodingException {
        HashMap<Integer, Stream> streamMap = new HashMap<>();
        JsonArray streams = data.getAsJsonArray("stream");
        for (JsonElement streamJson : streams) {
            if (streamJson.getAsJsonObject().get("channel_type") != null && streamJson.getAsJsonObject().get("channel_type").getAsString().equals("tail"))
                continue;
            List<Seg> segList = new ArrayList<>();

            JsonArray segJsons = streamJson.getAsJsonObject().getAsJsonArray("segs");
            for (JsonElement segJson : segJsons) {
                String url = segJson.getAsJsonObject().get("cdn_url").getAsString();
                double duration = segJson.getAsJsonObject().get("total_milliseconds_audio").getAsInt() / 1000;
                Seg seg = new Seg(url, duration);
                segList.add(seg);
            }

            int size = streamJson.getAsJsonObject().get("size").getAsInt();
            int height = streamJson.getAsJsonObject().get("height").getAsInt();
            int width = streamJson.getAsJsonObject().get("width").getAsInt();
            Stream stream = new Stream(segList, size, height, width);
            stream.setSize(size);
            streamMap.put(calQualityByHeight(height), stream);
        }
        return streamMap;
    }

    // Check error if response return error data
    private void checkError(JsonObject data) {
        if (data.has("error")) {
            LogUtil.e(TAG, "extract error: " + data.toString());
            JsonObject jsonObject = data.getAsJsonObject("error");
            String errorMsg = jsonObject.get("note").getAsString();
            // TODO: 1/25/17 Try to avoid hard-coding
            if (errorMsg.contains("该视频已经加密")) {
                throw new ExtractException("Youku said: Sorry, this video is private");
            } else if (errorMsg.contains("抱歉,该视频仅限中国大陆地区播放,请您观看其他视频!")) {
                throw new ExtractException("Youku said: Sorry, this video is available in China only");
            } else {
                throw new ExtractException("Youku server reported error " + jsonObject.get("error").getAsString());
            }
        }

    }

    // this may not be testable because it generates string randomly.
    private String getYsuid() {
        StringBuilder sb = new StringBuilder();
        int time = (int) (new Date().getTime() / 1000);
        sb.append(time);
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int offset = random.nextInt(letterTable.length);
            char c = letterTable[offset];
            sb.append(c);
        }
        return sb.toString();
    }

    @NonNull
    @Override
    Request buildRequest(@NonNull String baseUrl) {
        if (baseUrl == null) throw new IllegalArgumentException();
        String ysuid = getYsuid();
        Log.d(TAG, "set ysuid: " + ysuid);
        return new Request.Builder().url(baseUrl)
                .addHeader("Cookie", "xreferrer=http://www.youku.com;__ysuid=" + ysuid)
                .addHeader("Referer", baseUrl)
                .build();
    }

    @Override
    String constructBasicUrl(@NonNull String url) {
        mId = searchValue(url, ID_REGEX);
        if (mId == null)
            throw new IllegalArgumentException("Can't find id of this video.Please check");
        String clientIp = "192.168.1.1";
        String ccode = "0507";
        String urlh, cna = null;
        try {
            urlh = downloadData("https://log.mmstat.com/eg.js");
            cna = searchValue(urlh, "(?<=goldlog.Etag=\")[^\"]+");
        } catch (IOException e) {
            e.printStackTrace();
        }
        long time = System.currentTimeMillis() / 1000;
        String basicUrl = "https://ups.youku.com/ups/get.json?" +
                "vid=" + mId
                + "&ccode=" + ccode
                + "&client_ip=" + clientIp
                + "&utid=" + cna
                + "&client_ts=" + time;
        Log.i(TAG, "basic url" + basicUrl);
        return basicUrl;
    }

    private JsonObject getData(String response) {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(response).getAsJsonObject().getAsJsonObject("data");
    }

    private String getTitle(JsonObject data) {
        return data.getAsJsonObject("video").get("title").getAsString();
    }

}




