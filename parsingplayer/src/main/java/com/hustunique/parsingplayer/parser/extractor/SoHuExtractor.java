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
import com.hustunique.parsingplayer.parser.ExtractException;
import com.hustunique.parsingplayer.parser.entity.Seg;
import com.hustunique.parsingplayer.parser.entity.Stream;
import com.hustunique.parsingplayer.parser.entity.VideoInfoImpl;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CoXier on 17-2-7.
 */

public class SoHuExtractor extends Extractor {
    public static final String VALID_URL = "https?://(my\\.)?tv\\.sohu\\.com/.+/(n)?\\d+\\.shtml.*";

    public static String TEST_URL = "http://my.tv.sohu.com/us/232799889/78693464.shtml";
    private String mTitle;
    private boolean mMytv;
    private String mId;

    @Override
    String constructBasicUrl(@NonNull String url) {
        mId = extractId(url);
        mMytv = checkMytv(url);
        return constructUrl(mId);
    }

    @Nullable
    @Override
    VideoInfoImpl createInfo(@NonNull Response response) throws IOException {
        JsonObject vidDataJson = parseResponse(response.body().string());
        checkError(vidDataJson);
        Map<Integer,Stream> streamMap = getSegsMap(vidDataJson);
        return new VideoInfoImpl(mUrl, streamMap, mTitle, mId);
    }


    @NonNull
    @Override
    Request buildRequest(@NonNull String baseUrl) {
        return new Request.Builder().url(baseUrl).build();
    }

    @NonNull
    private String extractId(String url) {
        String result = null;
        try {
            result = new String(downloadData(url).getBytes("GB2312"), "GB2312");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mTitle = getTitle(result);
        return searchValue(searchValue(result, "var vid ?= ?[\"\\'](\\d+)[\"\\']"), "\\d+");
    }

    private boolean checkMytv(String url) {
        Pattern pattern = Pattern.compile("//my\\.");
        Matcher matcher = pattern.matcher(url);
        return matcher.find();
    }

    private void checkError(JsonObject vidDataJson) {
        String play = vidDataJson.get("play").getAsString();

        if (!play.equals("1")) {
            String status = vidDataJson.get("status").getAsString();
            if (status.equals("12")) {
                throw new ExtractException("Sohu said: There's something wrong in the video.");
            } else
                throw new ExtractException("Sohu said: The video is only licensed to users in Mainland China.");
        }

    }

    private String getTitle(String response) {
        String title = searchValue(response, "(?<=<meta property=\"og:title\" content=\").+?(?=\" />)");
        return title.replace(" - 搜狐视频", "");
    }

    private JsonObject fetchData(String vid) {
        String basicUrl = constructUrl(vid);
        try {
            return parseResponse(downloadData(basicUrl));
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new ExtractException("Can't fetch data");
    }

    private String constructUrl(String id) {
        if (mMytv)
            return String.format("http://my.tv.sohu.com/play/videonew.do?vid=%s", id);
        else
            return String.format("http://hot.vrs.sohu.com/vrs_flash.action?vid=%s", id);
    }

    private Map<Integer, Stream> getSegsMap(JsonObject vidDataJson) {
        int partCount = vidDataJson.getAsJsonObject("data").get("totalBlocks").getAsInt();
        JsonArray durationArray = vidDataJson.getAsJsonObject("data").getAsJsonArray("clipsDuration");
        String[] formatArray = new String[]{"nor", "high", "super", "ori"};
        HashMap<Integer, Stream> segsMap = new HashMap<>();
        int hd = 0;
        for (String formatId : formatArray) {
            JsonElement formatIdVidElement = vidDataJson.getAsJsonObject("data").get(formatId + "Vid");
            if (formatIdVidElement == null)
                continue;
            String vidId = formatIdVidElement.getAsString();
            // 0 means this format doesn't exist
            if (vidId.equals("0"))
                continue;
            JsonObject formatData = vidId.equals(mId) ? vidDataJson : fetchData(vidId);
            List<Seg> segList = new ArrayList<>();
            for (int i = 0; i < partCount; i++) {
                String allot = formatData.get("allot").getAsString();
                JsonObject data = formatData.getAsJsonObject("data");
                JsonArray clipUrlArray = data.getAsJsonArray("clipsURL");
                JsonArray suArray = data.getAsJsonArray("su");

                String videoUrl = "newflv.sohu.ccgslb.net";
                String cdnId = null;
                int retries = 0;

                while (videoUrl.contains("newflv.sohu.ccgslb.net")) {
                    String url = "http://"
                            + allot
                            + "/?"
                            + "prot=9"
                            + "&file=" + URLEncoder.encode(clipUrlArray.get(i).getAsString())
                            + "&new=" + URLEncoder.encode(suArray.get(i).getAsString())
                            + "&prod=flash"
                            + "&rb=1";
                    if (cdnId != null)
                        url = url + "&idc=" + URLEncoder.encode(cdnId);
                    try {
                        JsonObject partInfo = parseResponse(downloadData(url));
                        videoUrl = partInfo.get("url").getAsString();
                        if (partInfo.get("nid") != null)
                            cdnId = partInfo.get("nid").getAsString();
                        else
                            cdnId = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Seg seg = new Seg(videoUrl, durationArray.get(i).getAsDouble());
                    segList.add(seg);

                    retries += 1;
                    if (retries > 5)
                        throw new ExtractException("Failed to get video URL");
                }
            }
            segsMap.put(hd, new Stream(segList));
            hd++;
        }
        return segsMap;
    }
}
