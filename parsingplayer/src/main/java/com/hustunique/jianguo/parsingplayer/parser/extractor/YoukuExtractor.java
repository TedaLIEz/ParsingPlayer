package com.hustunique.jianguo.parsingplayer.parser.extractor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hustunique.jianguo.parsingplayer.parser.VideoParser;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by CoXier on 17-1-17.
 */

public class YoukuExtractor implements IExtractor {
    public static final String VALID_URL = "(?:http://(?:v|player)\\.youku\\.com/(?:v_show/id_|player\\.php/sid/)|youku:)([A-Za-z0-9]+)(?:\\.html|/v\\.swf|)";
    private static final String ID_REGEX = "((?<=id_)|(?<=sid/))[A-Za-z0-9]+";

    private String id;


    @Override
    public void extract(@NonNull String url, @Nullable final VideoParser.ExtractCallback callback) {
        id = extractId(url);
        String basicUrl = constructBasicUrl();
        // TODO: 1/17/17 Construction here can be refactored
        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(basicUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = jsonParser.parse(response.body().string()).getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonObject("data").getAsJsonArray("stream");
                Logger.json(jsonArray.toString());
                if (callback != null) {
                    callback.onSuccess(null);
                }
            }
        });

    }

    private String constructBasicUrl() {
        return "http://play.youku.com/play/get.json?vid="+id+"&ct=12";
    }

    private String extractId(String url){
        Pattern pattern = Pattern.compile(ID_REGEX);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()){
            return matcher.group(0);
        }
        return null;
    }
}
