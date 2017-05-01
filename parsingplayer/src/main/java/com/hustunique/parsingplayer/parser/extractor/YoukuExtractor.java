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
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hustunique.parsingplayer.util.LogUtil;
import com.hustunique.parsingplayer.util.Util;
import com.hustunique.parsingplayer.parser.ExtractException;
import com.hustunique.parsingplayer.parser.entity.Seg;
import com.hustunique.parsingplayer.parser.entity.Stream;
import com.hustunique.parsingplayer.parser.entity.VideoInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private HashMap<String, String> mFiledMap;

    private String mToken;
    private String mSid;
    private String mOip;

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

        mHdMap.put(FORMAT_3GP, VideoInfo.HD_LOW);
        mHdMap.put(FORMAT_3GPHD, VideoInfo.HD_MEDIUM);
        mHdMap.put(FORMAT_FLV, VideoInfo.HD_LOW);
        mHdMap.put(FORMAT_FLVHD, VideoInfo.HD_LOW);
        mHdMap.put(FORMAT_MP4, VideoInfo.HD_MEDIUM);
        mHdMap.put(FORMAT_MP4HD, VideoInfo.HD_MEDIUM);
        mHdMap.put(FORMAT_MP4HD2, VideoInfo.HD_MEDIUM);
        mHdMap.put(FORMAT_MP4HD3, VideoInfo.HD_MEDIUM);
        mHdMap.put(FORMAT_HD2, VideoInfo.HD_STANDARD);
        mHdMap.put(FORMAT_HD3, VideoInfo.HD_HIGH);
    }

    private String mId;


    @Override
    @Nullable
    VideoInfo createInfo(@NonNull Response response) throws IOException {
        JsonObject data = getData(response.body().string());
        if (data == null) return null;
        checkError(data);
        String encrypt = getEncrypt(data);
        String[] sidAndToken = getSidAndToken(encrypt);
        if (sidAndToken.length != 2) {
            throw new ExtractException("Illegal response data!");
        }
        mSid = sidAndToken[0];
        mToken = sidAndToken[1];
        mOip = getOip(data);
        LogUtil.i(TAG, "sid: " + mSid + " ,mToken: " + mToken + " ,oip: " + mOip);
        mFiledMap = constructFiledMap(data);
        String title = getTitle(data);
        Map<Integer, Stream> streamMap = getSegMap(data);
        LogUtil.d(TAG, "hd length:" + streamMap.keySet().size());
        return new VideoInfo(mId,streamMap, title);
    }

    @VisibleForTesting
    String[] getSidAndToken(@NonNull String encryptStr) {
        String s = new String(rc4("becaf9be", Base64.decode(encryptStr, Base64.DEFAULT)));
        String[] rst = s.split("_");
        return rst;
    }

    private Map<Integer, Stream> getSegMap(JsonObject data) throws UnsupportedEncodingException {
        HashMap<Integer, Stream> streamMap = new HashMap<>();
        JsonArray streams = data.getAsJsonArray("stream");
        int h = 0;
        for (JsonElement stream : streams) {
            if (stream.getAsJsonObject().get("channel_type") != null && stream.getAsJsonObject().get("channel_type").getAsString().equals("tail"))
                continue;
            List<Seg> segList = new ArrayList<>();
            String format = stream.getAsJsonObject().get("stream_type").getAsString();
            JsonArray segs = stream.getAsJsonObject().getAsJsonArray("segs");
            int n = 0;
            for (JsonElement seg : segs) {
                String key = seg.getAsJsonObject().get("key").getAsString();
                int hd = mHdMap.get(format);
                String fileId = getFileid(format, n);
                String ep = getEp(String.format("%s_%s_%s", mSid, fileId, mToken));
                String videoUrl = "http://k.youku.com/player/getFlvPath/" +
                        "sid/" + mSid +
                        "_00" +
                        "/st/" + mExtMap.get(format)
                        + "/fileid/" + fileId + "?" +
                        "k=" + URLEncoder.encode(key, "utf-8") +
                        "&hd=" + hd +
                        "&myp=0&ypp=0&ctype=12&ev=1" +
                        "&token=" + URLEncoder.encode(mToken, "utf-8") +
                        "&oip=" + URLEncoder.encode(mOip, "utf-8") +
                        "&ep=" + URLEncoder.encode(ep, "utf-8");
                LogUtil.d(TAG, "build url: " + videoUrl + " format: " + format);
                int duration = Integer.parseInt(seg.getAsJsonObject().get("total_milliseconds_video").getAsString()) / 1000;
                Seg s = new Seg(videoUrl, duration);
                segList.add(s);
                n++;
            }
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
        return String.format("http://play.youku.com/play/get.json?vid=%s&ct=12", mId);
    }


    private byte[] rc4(String key, String data) {
        byte[] s2 = new byte[0];
        try {
            s2 = data.getBytes("ascii");
        } catch (UnsupportedEncodingException e) {
            LogUtil.wtf(TAG, e);
            return s2;
        }
        return rc4(key, s2);
    }

    private String getOip(JsonObject data) {
        return data.getAsJsonObject("security").get("ip").getAsString();
    }

    private String getFileid(String format, int index) {
        String sIndex = Integer.toString(index, 16).toUpperCase(Locale.getDefault());
        if (sIndex.length() == 1)
            sIndex = "0" + sIndex;
        String streamFileids = mFiledMap.get(format);
        return streamFileids.substring(0, 8) + sIndex + streamFileids.substring(10);
    }

    @VisibleForTesting
    String getEp(@NonNull String data) {
        byte[] epT = rc4("bf7e5f01", data);
        return Base64.encodeToString(epT, Base64.NO_WRAP);
    }

    private HashMap<String, String> constructFiledMap(JsonObject data) {
        HashMap<String, String> fileidMap = new HashMap<>();
        JsonArray streams = data.getAsJsonArray("stream");
        for (JsonElement stream : streams) {
            if (stream.getAsJsonObject().get("channel_type") != null && stream.getAsJsonObject().get("channel_type").getAsString().equals("tail"))
                continue;
            String format = stream.getAsJsonObject().get("stream_type").getAsString();
            String fileid = stream.getAsJsonObject().get("segs").getAsJsonArray()
                    .get(0).getAsJsonObject()
                    .get("fileid").getAsString();
            fileidMap.put(format, fileid);
        }
        return fileidMap;
    }

    private JsonObject getData(String response) {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(response).getAsJsonObject().getAsJsonObject("data");
    }

    private String getTitle(JsonObject data) {
        return data.getAsJsonObject("video").get("title").getAsString();
    }

    private String getEncrypt(JsonObject data) {
        return data.getAsJsonObject("security").get("encrypt_string").getAsString();
    }

    private byte[] rc4(@NonNull String key, byte[] data) {
        byte[] s1 = key.getBytes();
        return Util.rc4(s1, data);
    }

}




