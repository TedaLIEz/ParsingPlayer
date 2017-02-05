package com.hustunique.parsingplayer.player.io;

import java.util.concurrent.Callable;

/**
 * Created by JianGuo on 2/5/17.
 * Async callback used in thread pool
 */

public final class CallbackTask<T> implements Runnable {
    private final Callable<T> mCallable;
    private final LoadingCallback<T> mCallback;

    public CallbackTask(Callable<T> callable, LoadingCallback<T> callback) {
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
