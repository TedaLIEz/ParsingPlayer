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

import java.util.concurrent.Callable;

/**
 * Created by JianGuo on 2/5/17.
 * Async callback used in thread pool
 * Deprecated as we want to run the callback in the main thread.
 */
@Deprecated
final class WriteTask<T> implements Runnable {
    private final Callable<T> mCallable;
    private final LoadingCallback<T> mCallback;

    WriteTask(Callable<T> callable, LoadingCallback<T> callback) {
        mCallable = callable;
        mCallback = callback;
    }


    @Override
    public void run() {
        try {
            T t = mCallable.call();
            if (t == null) {
                mCallback.onFailed(new NullPointerException());
                return;
            }
            mCallback.onSuccess(t);
        } catch (Exception e) {
            mCallback.onFailed(e);
        }

    }
}
