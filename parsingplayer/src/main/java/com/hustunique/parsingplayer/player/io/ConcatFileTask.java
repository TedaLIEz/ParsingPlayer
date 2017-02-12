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

package com.hustunique.parsingplayer.player.io;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.hustunique.parsingplayer.LogUtil;
import com.hustunique.parsingplayer.player.ParsingVideoView;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by JianGuo on 1/23/17.
 * Utility for ffconcat protocol, so far the way we play multiple videos is to write
 * a temporary file and set a file uri path to our {@link ParsingVideoView}.
 * This class is utilized in writing information to file
 */
@Deprecated
public final class ConcatFileTask extends AsyncTask<String, Void, FileDescriptor> {
    private static final String TAG = "ConcatFileTask";
    private Context mContext;
    private Callback mCallback;

    public ConcatFileTask(@NonNull Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
    }


    @Override
    protected FileDescriptor doInBackground(String... params) {
//        String filename = params[0];
//        String data = params[1];
//        FileDescriptor fd;
//        try {
//            fd = Util.writeToFile(filename, data, mContext);
//        } catch (IOException e) {
//            LogUtil.wtf(TAG, e);
        return null;
//        }
//        return fd;
    }

    @Override
    protected void onPostExecute(FileDescriptor fileDescriptor) {
        if (fileDescriptor == null) return;
        if (mCallback != null) {
            try {
                mCallback.onFileSaved(fileDescriptor);
            } catch (IOException e) {
                LogUtil.wtf(TAG, e);
            }
        }
    }

    public interface Callback {
        void onFileSaved(FileDescriptor fd) throws IOException;
    }

}
