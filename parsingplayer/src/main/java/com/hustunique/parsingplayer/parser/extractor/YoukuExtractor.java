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
    /**
     * There are four qualities 0 ~3 for these formats.
     * <ul>
     * <li>3gp,flv,flvhd: 0</li>
     * <li>3gphd,mp4,mp4hd,mp4hd2,mp4hd3: 1</li>
     * <li>hd2: 2</li>
     * <li>hd3: 3</li>
     * </ul>
     */
    private static final String FORMAT_3GP = "3gp";
    private static final String FORMAT_3GPHD = "3gphd";
    private static final String FORMAT_FLV = "flv";
    private static final String FORMAT_FLVHD = "flvhd";
    private static final String FORMAT_MP4 = "mp4";
    private static final String FORMAT_MP4HD = "mp4hd";
    private static final String FORMAT_MP4HD2 = "mp4hd2";
    private static final String FORMAT_MP4HD3 = "mp4hd3";
    private static final String FORMAT_HD2 = "hd2";
    private static final String FORMAT_HD3 = "hd3";

    private static HashMap<String, String> mExtMap = new HashMap<>();
    private static HashMap<String, Integer> mHdMap = new HashMap<>();


    static {
        mExtMap.put(FORMAT_3GP, "flv");
        mExtMap.put(FORMAT_3GPHD, "mp4");
        mExtMap.put(FORMAT_FLV, "flv");
        mExtMap.put(FORMAT_FLVHD, "flv");
        mExtMap.put(FORMAT_MP4, "mp4");
        mExtMap.put(FORMAT_MP4HD, "mp4");
        mExtMap.put(FORMAT_MP4HD2, "flv");
        mExtMap.put(FORMAT_MP4HD3, "flv");
        mExtMap.put(FORMAT_HD2, "flv");
        mExtMap.put(FORMAT_HD3, "flv");

        mHdMap.put(FORMAT_3GP, VideoInfoImpl.HD_LOW);
        mHdMap.put(FORMAT_3GPHD, VideoInfoImpl.HD_MEDIUM);
        mHdMap.put(FORMAT_FLV, VideoInfoImpl.HD_LOW);
        mHdMap.put(FORMAT_FLVHD, VideoInfoImpl.HD_LOW);
        mHdMap.put(FORMAT_MP4, VideoInfoImpl.HD_MEDIUM);
        mHdMap.put(FORMAT_MP4HD, VideoInfoImpl.HD_MEDIUM);
        mHdMap.put(FORMAT_MP4HD2, VideoInfoImpl.HD_MEDIUM);
        mHdMap.put(FORMAT_MP4HD3, VideoInfoImpl.HD_MEDIUM);
        mHdMap.put(FORMAT_HD2, VideoInfoImpl.HD_STANDARD);
        mHdMap.put(FORMAT_HD3, VideoInfoImpl.HD_HIGH);
    }

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
        int h = 0;
        for (JsonElement stream : streams) {
            if (stream.getAsJsonObject().get("channel_type") != null && stream.getAsJsonObject().get("channel_type").getAsString().equals("tail"))
                continue;
            List<Seg> segList = new ArrayList<>();
            int duration = stream.getAsJsonObject().get("milliseconds_audio").getAsInt() / 1000;
            String m3u8Url = stream.getAsJsonObject().get("m3u8_url").getAsString();
            segList.add(new Seg(m3u8Url, duration));
            streamMap.put(h, new Stream(segList));
            h++;
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
        String ccode;
        if (url.contains("tudou.com")) {
            ccode = "0402";
        } else {
            ccode = "0401";
        }
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




