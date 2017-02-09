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
import com.hustunique.parsingplayer.parser.entity.VideoInfo;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CoXier on 17-2-7.
 */

public class SoHuExtractor extends Extractor {
    public static final String VALID_URL = "https?://(my\\.)?tv\\.sohu\\.com/.+/(n)?\\d+\\.shtml.*";


    private String mTitle;
    private boolean mMytv;
    private String mId;
    private static OkHttpClient mClient;

    static {
        mClient = new OkHttpClient();
    }

    @Override
    String constructBasicUrl(@NonNull String url) {
        mId = extractId(url);
        mMytv = checkMytv(url);
        return constructUrl(mId);
    }

    @Nullable
    @Override
    VideoInfo createInfo(@NonNull Response response) throws IOException {
        JsonObject vidDataJson = parseResponse(response);
        checkError(vidDataJson);
        Map<Integer, List<Seg>> segsMap = getSegsMap(vidDataJson);
        return new VideoInfo(segsMap,mTitle);
    }


    @NonNull
    @Override
    Request buildRequest(@NonNull String baseUrl) {
        return new Request.Builder().url(baseUrl).build();
    }

    @NonNull
    private String extractId(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;
        try {
            response = mClient.newCall(request).execute();
            String result = new String(response.body().bytes(), "GB2312");
            Pattern pattern = Pattern.compile("var vid ?= ?[\"\\'](\\d+)[\"\\']");
            Matcher matcher = pattern.matcher(result);
            matcher.find();

            Pattern patternVid = Pattern.compile("\\d+");
            Matcher matcherVid = patternVid.matcher(matcher.group(0));
            matcherVid.find();

            mTitle = getTitle(result);
            return matcherVid.group(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Can't find id of this video.Please check");
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
        Pattern pattern = Pattern.compile("(?<=<meta property=\"og:title\" content=\").+?(?=\" />)");
        Matcher matcher = pattern.matcher(response);
        matcher.find();
        return matcher.group(0).replace(" - 搜狐视频", "");
    }

    private JsonObject fetchData(String vid) {
        String basicUrl = constructUrl(vid);
        Request request = new Request.Builder().url(basicUrl).build();
        try {
            Response response = mClient.newCall(request).execute();
            return parseResponse(response);
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

    private Map<Integer, List<Seg>> getSegsMap(JsonObject vidDataJson) {
        int partCount = vidDataJson.getAsJsonObject("data").get("totalBlocks").getAsInt();
        JsonArray durationArray = vidDataJson.getAsJsonObject("data").getAsJsonArray("clipsDuration");
        String[] formatArray = new String[]{"nor", "high", "super", "ori"};
        HashMap<Integer,List<Seg>> segsMap = new HashMap<>();
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

                    Request request = new Request.Builder().url(url).build();
                    try {
                        Response response = mClient.newCall(request).execute();
                        JsonObject partInfo = parseResponse(response);
                        videoUrl = partInfo.get("url").getAsString();
                        if (partInfo.get("nid") != null)
                            cdnId = partInfo.get("nid").getAsString();
                        else
                            cdnId = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Seg seg = new Seg(videoUrl,durationArray.get(i).getAsDouble());
                    segList.add(seg);

                    retries += 1;
                    if (retries > 5)
                        throw new ExtractException("Failed to get video URL");
                }
            }
            segsMap.put(hd,segList);
            hd++;
        }
        return segsMap;
    }
}
