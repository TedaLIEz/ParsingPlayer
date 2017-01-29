package com.hustunique.parsingplayer.player;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.hustunique.parsingplayer.LogUtil;
import com.hustunique.parsingplayer.Util;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by JianGuo on 1/23/17.
 * Utility for ffconcat protocol, so far the way we play multiple videos is to write
 * a temporary file and set a file uri path to our {@link ParsingVideoView}.
 * This class is utilized in writing information to file
 */
// TODO: 1/23/17 DiskLruCache may be needed here
// TODO: 1/29/17 Maybe use some pojo class here as param is better than using string
public class ConcatFileTask extends AsyncTask<String, Void, FileDescriptor> {
    private static final String TAG = "ConcatFileTask";
    private Context mContext;
    private Callback mCallback;
    public ConcatFileTask(@NonNull Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
    }



    @Override
    protected FileDescriptor doInBackground(String... params) {
        String filename = params[0];
        String data = params[1];
        FileDescriptor fd;
        try {
            fd = Util.writeToFile(filename, data, mContext);
        } catch (IOException e) {
            LogUtil.wtf(TAG, e);
            return null;
        }
        return fd;
    }

    @Override
    protected void onPostExecute(FileDescriptor fileDescriptor) {
        if (fileDescriptor == null) return;
        if (mCallback != null) {
            mCallback.onFileSaved(fileDescriptor);
        }
    }

    public interface Callback {
        void onFileSaved(FileDescriptor fd);
    }

}
