

package com.hustunique.parsingplayer.parser.extractor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hustunique.parsingplayer.parser.entity.Seg;
import com.hustunique.parsingplayer.parser.entity.VideoInfo;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by JianGuo on 1/16/17.
 * Interface for extracting video info from websites
 */

public abstract class Extractor {
    private static final String TAG = "Extractor";
    private OkHttpClient mClient;

    public Extractor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        mClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
    }


    public VideoInfo extract(@NonNull String url) {
        String baseUrl = constructBasicUrl(url);
        final Request request = buildRequest(baseUrl);
        return extract(request);
    }

    @VisibleForTesting
    private VideoInfo extract(@NonNull Request request) {
        try {
            Response response = mClient.newCall(request).execute();
            return createInfo(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected JsonObject parseResponse(Response response) throws IOException{
        JsonParser parser = new JsonParser();
        return parser.parse(response.body().string()).getAsJsonObject();
    }

    abstract String constructBasicUrl(@NonNull String url);

    @Nullable
    abstract VideoInfo createInfo(@NonNull Response response) throws IOException;
    @NonNull
    abstract Request buildRequest(@NonNull String baseUrl);
}
