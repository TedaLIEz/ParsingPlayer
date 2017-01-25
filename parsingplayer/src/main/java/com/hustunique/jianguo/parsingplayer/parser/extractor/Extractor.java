

package com.hustunique.jianguo.parsingplayer.parser.extractor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hustunique.jianguo.parsingplayer.parser.entity.VideoInfo;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by JianGuo on 1/16/17.
 * Interface for extracting video info from websites
 */

public abstract class Extractor implements Callback {
    private ExtractCallback mCallback;
    private OkHttpClient mClient;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    public interface ExtractCallback {
        void onSuccess(VideoInfo videoInfo);
        void onError(Throwable e);
    }

    public Extractor() {
        mClient = new OkHttpClient();
    }

    /**
     * Extract videoinfo from given url,
     * call {@link ExtractCallback#onSuccess(VideoInfo)}
     * when succeed.
     * @param url the given url
     * @param callback the callback, see {@link ExtractCallback} for details
     */
    public void extract(@NonNull String url, @Nullable final ExtractCallback callback) {
        String baseUrl = constructBasicUrl(url);
        final Request request = buildRequest(baseUrl);
        mClient.newCall(request).enqueue(this);
        mCallback = callback;
    }

    abstract String constructBasicUrl(@NonNull String url);

    @Nullable
    abstract VideoInfo createInfo(@NonNull Response response) throws IOException;
    @NonNull
    abstract Request buildRequest(@NonNull String baseUrl);

    @Override
    public void onFailure(Call call, IOException e) {
        if (mCallback != null) mCallback.onError(e);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        final VideoInfo videoInfo = createInfo(response);
        // make sure that we run the callback method on the main messaging queue.
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    mCallback.onSuccess(videoInfo);
                }
            }
        });

    }

}
