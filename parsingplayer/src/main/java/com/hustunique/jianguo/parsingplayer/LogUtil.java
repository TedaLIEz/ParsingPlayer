/*
 * Copyright 2016 TedaLIEz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hustunique.jianguo.parsingplayer;

import android.util.Log;

/**
 * Created by JianGuo on 1/16/17.
 * Wrapper for {@link Log}
 */

public class LogUtil {
    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, e);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg, e);
        }
    }

    public static void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    public static void v(String tag, String msg, Throwable e) {
        Log.v(tag, msg, e);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void i(String tag, String msg, Throwable e) {
        Log.i(tag, msg, e);
    }

    public static void wtf(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, msg);
        }
    }

    public static void wtf(String tag, String msg, Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, msg, e);
        }
    }

    public static void wtf(String tag, Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.wtf(tag, e);
        }
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void w(String tag, String msg, Throwable e) {
        Log.w(tag, msg, e);
    }

}
