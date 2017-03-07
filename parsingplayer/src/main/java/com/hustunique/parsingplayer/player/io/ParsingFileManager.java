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
    private final ExecutorService mCleanupService = new ThreadPoolExecutor(0, 1,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private ParsingFileManager(File mRootDirectory) {
        mFileService = ConcatExecutorService.createService();
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


    public void write(String filename, String content, LoadingCallback<String> callback) {
        WriteTask<String> task = createWriteTask(filename, content, callback);
        mFileService.execute(task);
    }

    private WriteTask<String> createWriteTask(final String filename, final String content,
                                              LoadingCallback<String> callback) {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws FileNotFoundException {
                return Util.writeToFile(mRootDirectory, filename, content);
            }
        };
        return new WriteTask<>(callable, callback);
    }

}
