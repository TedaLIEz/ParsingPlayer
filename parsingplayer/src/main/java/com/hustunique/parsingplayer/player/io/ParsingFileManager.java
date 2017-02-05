package com.hustunique.parsingplayer.player.io;

import android.util.Log;

import com.hustunique.parsingplayer.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by JianGuo on 2/5/17.
 * File manager used in player.
 */

public final class ParsingFileManager {
    private static final String TAG = "ParsingFileManager";
    private final ExecutorService fileService;
    private final File directory;
    private final ExecutorService cleanupService = new ThreadPoolExecutor(0, 1,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private final Callable<Void> cleanupCallable = new Callable<Void>() {
        @Override
        public Void call() throws Exception {
            deleteContents(directory);
            Log.d(TAG, "done with cleanup");
            return null;
        }
    };

    private ParsingFileManager(File directory) {
        fileService = ConcatExecutorService.createService();
        this.directory = directory;
    }

    private static ParsingFileManager mManager;

    public static ParsingFileManager getInstance(File directory) {
        if (mManager == null) {
            directory.mkdirs();
            mManager = new ParsingFileManager(directory);
        }
        return mManager;
    }


    public void write(String filename, String content, LoadingCallback<String> callback) {
        CallbackTask<String> task = createWriteTask(filename, content, callback);
        fileService.execute(task);
    }

    public void read(String filename, LoadingCallback<String> callback) {
        // TODO: 2/5/17 Implement read string
    }

    public void cleanUp() {
        cleanupService.submit(cleanupCallable);
    }


    private CallbackTask<String> createWriteTask(final String filename, final String content,
                                                         LoadingCallback<String> callback) {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws FileNotFoundException {
                return Util.writeToFile(directory, filename, content);
            }
        };
        return new CallbackTask<>(callable, callback);
    }

    private CallbackTask<String> createReadTask(final String filename,
                                                LoadingCallback<String> callback) {
        return null;
    }

    private static void deleteContents(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IllegalArgumentException("not a directory " + dir);
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteContents(file);
            }
            if (!file.delete()) {
                throw new IOException("failed to delete file: " + file);
            }
        }
    }


}
