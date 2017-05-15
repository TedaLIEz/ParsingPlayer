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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hustunique.parsingplayer.util.Util;
import com.hustunique.parsingplayer.parser.entity.Seg;
import com.hustunique.parsingplayer.parser.entity.Stream;
import com.hustunique.parsingplayer.parser.entity.VideoInfoImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CoXier on 17-2-12.
 */

public class BilibiliExtractor extends Extractor {

    public static final String VALID_URL = "https?://(?:www|bangumi)\\.bilibili\\.(?:tv|com)/(?:video/av|anime/\\d+/play#)\\d+";

    private static final String APP_KEY = "84956560bc028eb7";
    private static final String BILIBILI_KEY = "94aba54af9065f71de72f5508f1cd42e";

    public static String[] TEST_URL = {"http://www.bilibili.tv/video/av1074402/"
            , "http://bangumi.bilibili.com/anime/5802/play#100643"};

    private static final String ID_REGEX = "(?<=av|#)\\d+";

    private String TAG = "Bilibili";

    private String mId;
    private String mCid;

    private String mTitle;

    @Override
    String constructBasicUrl(@NonNull String url) {
        mId = searchValue(url, ID_REGEX);
        String webPage = null;
        try {
            webPage = downloadData(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mTitle = searchValue(webPage,"(?<=<h1 title=\")[^\"]+");
        if (url.contains("anime/")) {
            HashMap<String,String> headers = new HashMap<>();
            headers.put("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
            HashMap<String,String> postData = new HashMap<>();
            postData.put("episode_id",mId);
            try {
                String js = downloadData("http://bangumi.bilibili.com/web_api/get_source",headers,postData);
                mCid = parseResponse(js).get("result").getAsJsonObject().get("cid").getAsString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mCid = searchValue(webPage, "(?<=cid=)[^&]+");
        }
        return constructBasicUrl(1);
    }

    @Nullable
    @Override
    VideoInfoImpl createInfo(@NonNull Response response) throws IOException {
        JsonObject data = parseResponse(response.body().string());
        Map<Integer,Stream> streamMap = getSegsMap(data);
        return new VideoInfoImpl(mUrl, streamMap, mTitle, mId);
    }

    @NonNull
    @Override
    Request buildRequest(@NonNull String baseUrl) {
        return new Request.Builder().url(baseUrl).build();
    }

    private String constructBasicUrl(int quality) {
        String payLoad = String.format("appkey=%s&cid=%s&otype=json&quality=%s&type=", APP_KEY, mCid, quality);
        String sign = Util.getMD5(payLoad + BILIBILI_KEY);
        return String.format("http://interface.bilibili.com/playurl?%s&sign=%s", payLoad, sign);
    }

    @NonNull
    private Map<Integer,Stream> getSegsMap(JsonObject data){
        HashMap<Integer,Stream> streamMap = new HashMap<>();
        List<Seg> segs;
        JsonArray qualityArray = data.getAsJsonArray("accept_quality");
        JsonObject dataTmp = null;
        for (JsonElement quality:qualityArray){
            segs = new ArrayList<>();
            if (quality.getAsInt() == 1){
                dataTmp = data;
            }else {
                String basicUrl = constructBasicUrl(quality.getAsInt());
                try {
                    dataTmp = parseResponse(downloadData(basicUrl));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            JsonArray durl = dataTmp.getAsJsonArray("durl");
            for (JsonElement urlPart:durl){
                String path = urlPart.getAsJsonObject().get("url").getAsString();
                double duration = urlPart.getAsJsonObject().get("length").getAsDouble() / 1000;
                Seg seg = new Seg(path,duration);
                segs.add(seg);
            }
            streamMap.put(quality.getAsInt(),new Stream(segs));
        }
        return streamMap;
    }

}
