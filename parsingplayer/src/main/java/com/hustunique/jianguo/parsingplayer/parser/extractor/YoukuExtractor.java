package com.hustunique.jianguo.parsingplayer.parser.extractor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hustunique.jianguo.parsingplayer.LogUtil;
import com.hustunique.jianguo.parsingplayer.Util;
import com.hustunique.jianguo.parsingplayer.parser.entity.Seg;
import com.hustunique.jianguo.parsingplayer.parser.entity.VideoInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static int HD_0 = 0;
    private static int HD_1 = 1;
    private static int HD_2 = 2;
    private static int HD_3 = 3;

    private static HashMap<String, String> mExtMap = new HashMap<>();
    private static HashMap<String, Integer> mHdMap = new HashMap<>();
    private HashMap<String, String> mFiledMap;

    private String mToken;
    private String mSid;
    private String mOip;

    static {
        mExtMap.put(VideoInfo.FORMAT_3GP, "flv");
        mExtMap.put(VideoInfo.FORMAT_3GPHD, "mp4");
        mExtMap.put(VideoInfo.FORMAT_FLV, "flv");
        mExtMap.put(VideoInfo.FORMAT_FLVHD, "flv");
        mExtMap.put(VideoInfo.FORMAT_MP4, "mp4");
        mExtMap.put(VideoInfo.FORMAT_MP4HD, "mp4");
        mExtMap.put(VideoInfo.FORMAT_MP4HD2, "flv");
        mExtMap.put(VideoInfo.FORMAT_MP4HD3, "flv");
        mExtMap.put(VideoInfo.FORMAT_HD2, "flv");
        mExtMap.put(VideoInfo.FORMAT_HD3, "flv");

        mHdMap.put(VideoInfo.FORMAT_3GP, HD_0);
        mHdMap.put(VideoInfo.FORMAT_3GPHD, HD_1);
        mHdMap.put(VideoInfo.FORMAT_FLV, HD_0);
        mHdMap.put(VideoInfo.FORMAT_FLVHD, HD_0);
        mHdMap.put(VideoInfo.FORMAT_MP4, HD_1);
        mHdMap.put(VideoInfo.FORMAT_MP4HD, HD_1);
        mHdMap.put(VideoInfo.FORMAT_MP4HD2, HD_1);
        mHdMap.put(VideoInfo.FORMAT_MP4HD3, HD_1);
        mHdMap.put(VideoInfo.FORMAT_HD2, HD_2);
        mHdMap.put(VideoInfo.FORMAT_HD3, HD_3);
    }


    @Override
    @Nullable
    VideoInfo createInfo(@NonNull Response response) throws IOException {
        JsonObject data = getData(response.body().string());
        LogUtil.i(TAG, "encrypt string: " + getEncrypt(data));
        checkError(data);
        String encrypt = getEncrypt(data);
        String[] sidAndToken = getSidAndToken(encrypt);
        if (sidAndToken.length != 2) {
            throw new ExtractException("Illegal response data!");
        }
        mSid = sidAndToken[0];
        mToken = sidAndToken[1];
        mOip = getOip(data);
        LogUtil.i(TAG, "sid: " + mSid +" ,mToken: " + mToken + " ,oip: " + mOip);
        mFiledMap = constructFiledMap(data);
        String title = getTitle(data);
        Map<String, List<Seg>> segsMap = getSegMap(data);
        return new VideoInfo(segsMap, title);
    }

    @VisibleForTesting
    String[] getSidAndToken(@NonNull String encryptStr) {
        String s = new String(rc4("becaf9be", Base64.decode(encryptStr, Base64.DEFAULT)));
        String[] rst = s.split("_");
        return rst;
    }

    private Map<String, List<Seg>> getSegMap(JsonObject data) throws UnsupportedEncodingException {
        HashMap<String, List<Seg>> segsMap = new HashMap<>();
        JsonArray streams = data.getAsJsonArray("stream");
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
                segsMap.put(format, segList);
                n++;
            }
        }
        return segsMap;
    }

    // Check error if response return error data
    private void checkError(JsonObject data) {
        if (data.has("error")) {
            JsonObject jsonObject = data.getAsJsonObject("error");
            String errorMsg = jsonObject.get("note").getAsString();
            // TODO: 1/25/17 Try to avoid hard-coding
            if (errorMsg.contains("该视频已经加密")) {
                throw new ExtractException("Youku said: Sorry, this video is private");
            } else if (errorMsg.contains("因版权原因无法观看此视频")) {
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
        String id = extractId(url);
        if (id == null)
            throw new IllegalArgumentException("Can't find id of this video.Please check");
        return String.format("http://play.youku.com/play/get.json?vid=%s&ct=12", id);
    }

    @Nullable
    private String extractId(String url) {
        if (url == null) throw new IllegalArgumentException();
        Pattern pattern = Pattern.compile(ID_REGEX);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
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
        String sIndex = Integer.toString(index, 16).toUpperCase();
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
            String fileid = stream.getAsJsonObject().get("stream_fileid").getAsString();
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




