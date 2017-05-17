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

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Pair;

import com.hustunique.parsingplayer.util.LogUtil;
import com.hustunique.parsingplayer.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by JianGuo on 2/5/17.
 * File manager used in player.
 */
// TODO: 3/7/17 Caching
public final class ParsingFileManager {
    private static final String TAG = "ParsingFileManager";
    private final ExecutorService mFileService;
    private final File mRootDirectory;
    private static final int MESSAGE_POST_RESULT = 1;
    private static Handler sHandler;
    private final ExecutorService mCleanupService = new ThreadPoolExecutor(0, 1,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private ParsingFileManager(File mRootDirectory) {
        mFileService = FileExecutor.createService();
        this.mRootDirectory = mRootDirectory;
        mRootDirectory.mkdirs();
    }

    private static ParsingFileManager mManager;

    public static ParsingFileManager getInstance(File directory) {
        if (mManager == null) {
            mManager = new ParsingFileManager(directory);
        }
        return mManager;
    }


    /**
     * Write a ffconcat config file
     *
     * @param fileName config fileName
     * @param content  the content of config
     * @param callback the loading callback
     */
    public void write(String fileName, String content, LoadingCallback<String> callback) {
        LogUtil.i(TAG, "set temp file content: \n" + content);
        Callable<String> task = createWriteTask(fileName, content, callback);
        mFileService.submit(task);
    }


    private Callable<String> createWriteTask(final String filename, final String content,
                                             final LoadingCallback<String> callback) {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws FileNotFoundException {
                String path = null;
                try {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    path = Util.writeToFile(mRootDirectory, filename, content);
                } catch (Throwable tr) {
                    tr.printStackTrace();
                    throw tr;
                } finally {
                    postResult(new Pair<>(path, callback));
                }
                return path;
            }
        };
        return callable;
    }

    private void postResult(Pair<String, LoadingCallback<String>> pair) {
        Message msg = getHandler().obtainMessage(MESSAGE_POST_RESULT,
                pair);
        msg.sendToTarget();
    }

    private static Handler getHandler() {
        synchronized (ParsingFileManager.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }

    private static class InternalHandler extends Handler {

        InternalHandler() {
            super(Looper.getMainLooper());
        }

        @SuppressWarnings({"unchecked"})
        @Override
        public void handleMessage(Message msg) {
            Pair<String, LoadingCallback<String>> callback = (Pair<String, LoadingCallback<String>>) msg.obj;
            callback.second.onSuccess(callback.first);

        }
    }


}
