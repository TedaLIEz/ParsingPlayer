package com.hustunique.jianguo.parsingplayer.parser.extractor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hustunique.jianguo.parsingplayer.parser.entity.VideoInfo;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CoXier on 17-1-17.
 */

public class YoukuExtractor extends Extractor {
    public static final String VALID_URL = "(?:http://(?:v|player)\\.youku\\.com/(?:v_show/id_|player\\.php/sid/)|youku:)([A-Za-z0-9]+)(?:\\.html|/v\\.swf|)";
    private static final String ID_REGEX = "((?<=id_)|(?<=sid/))[A-Za-z0-9]+";
    private static final String TAG = "YoukuExtractor";


    // TODO: 1/17/17 Build relationship between stream quality string to integer
    private int getVideoQuality(String quality) {
        return 0;
    }

    @Override
    @Nullable
    VideoInfo createInfo(@NonNull Response response) throws IOException {
        // TODO: 1/17/17 Create VideoInfo by response body
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(response.body().string()).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonObject("data").getAsJsonArray("stream");
        Logger.json(jsonArray.toString());
        return null;
    }

    @NonNull
    @Override
    OkHttpClient buildClient() {
        return new OkHttpClient();
    }

    @NonNull
    @Override
    Request buildRequest(@NonNull String baseUrl) {
        if (baseUrl == null) throw new IllegalArgumentException();
        return new Request.Builder().url(baseUrl).build();
    }

    @Override
    String constructBasicUrl(@NonNull String url) {
        if (url == null) throw new IllegalArgumentException();
        String id = extractId(url);
        return String.format("http://play.youku.com/play/get.json?vid=%s&ct=12", id);
    }

    @Nullable
    private String extractId(String url){
        Pattern pattern = Pattern.compile(ID_REGEX);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()){
            return matcher.group(0);
        }
        return null;
    }


}
