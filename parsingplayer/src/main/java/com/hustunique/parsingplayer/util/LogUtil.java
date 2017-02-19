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

package com.hustunique.parsingplayer.util;

import android.util.Log;

import com.hustunique.parsingplayer.BuildConfig;

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
