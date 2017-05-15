package com.hustunique.parsingplayer.parser.extractor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
 * Created by CoXier on 17-2-22.
 */

public class QQExtractor extends Extractor {
    public static final String VALID_URL = "https://v.qq.com/x/cover/.+/.+\\.html";
    public static final String TEST_URL = "https://v.qq.com/x/cover/xvlhj7cglyfwx8x/m0022eyxv9v.html";

    private static final String TAG = "QQExtractor";
    private String mVid;
    private String mTitle;

    @Override
    String constructBasicUrl(@NonNull String url) {
        mVid = extractVid(url);
        return "http://vv.video.qq.com/getinfo?otype=json&appver=3%2E2%2E19%2E333&platform=11&defnpayver=1&vid=" + mVid;
    }

    @Nullable
    @Override
    VideoInfoImpl createInfo(@NonNull Response response) throws IOException {
        String info = response.body().string();
        JsonObject videoJson = parseResponse(searchValue(info, "(?<=QZOutputJson=).*"));
        mTitle = videoJson.get("vl").getAsJsonObject().get("vi")
                .getAsJsonArray().get(0).getAsJsonObject().get("ti").getAsString();
        Map<Integer, Stream> streamMap = getStreamMap(videoJson);
        return new VideoInfoImpl(mUrl,streamMap, mTitle, mVid);
    }

    @NonNull
    @Override
    Request buildRequest(@NonNull String baseUrl) {
        return new Request.Builder().url(baseUrl).build();
    }

    private String extractVid(String url) {
        return searchValue(url, "(?<=/)([^/]+)(?=\\.html)");
    }

    private Map<Integer, Stream> getStreamMap(JsonObject videoJson) {
        HashMap<Integer, Stream> streamMap = new HashMap<>();
        JsonObject vi = videoJson.get("vl").getAsJsonObject()
                .get("vi").getAsJsonArray().get(0).getAsJsonObject();
        String partsPrefix = vi.get("ul").getAsJsonObject()
                .get("ui").getAsJsonArray().get(0).getAsJsonObject()
                .get("url").getAsString();
        JsonArray partFormats = videoJson.get("fl").getAsJsonObject().get("fi").getAsJsonArray();
        for (JsonElement partFormat : partFormats) {
            // now support 1080p and 720p
            String sHd = partFormat.getAsJsonObject().get("name").getAsString();
            if (!sHd.equals("fhd") && !sHd.equals("shd"))
                continue;
            List<Seg> segs = new ArrayList<>();
            int hd = sHd.equals("fhd") ? 3 : 2;
            int partFormatId = partFormat.getAsJsonObject().get("id").getAsInt();
            int partFormatSl = partFormat.getAsJsonObject().get("sl").getAsInt();
            if (partFormatSl == 0) {
                int part = 1;
                while (true) {
                    String fileName = mVid + ".p" + (partFormatId % 1000) + "." + part + ".mp4";
                    String keyApi = String.format("http://vv.video.qq.com/getkey?otype=json&platform=11&format=%s&vid=%s&filename=%s", partFormatId, mVid, fileName);
                    try {
                        String partInfo = downloadData(keyApi).replace("QZOutputJson=", "");
                        JsonObject keyJson = parseResponse(partInfo);
                        if (keyJson.get("key") == null)
                            break;
                        String vKey = keyJson.get("key").getAsString();
                        double br = keyJson.get("br").getAsDouble();
                        String url = String.format("%s/%s?vkey=%s", partsPrefix, fileName, vKey);
                        part++;
                        double duration = calculateDuration(url,br);
                        Seg seg = new Seg(url,duration);
                        segs.add(seg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            streamMap.put(hd,new Stream(segs));
        }
        return streamMap;
    }

    private double calculateDuration(String url,double br){
        Request request = buildRequest(url);
        try {
            Response response = mClient.newCall(request).execute();
            double contentLength = Double.parseDouble(response.header("Content-Length"));
            return contentLength / br;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("haven't get duration");
    }
}
