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

package com.hustunique.parsingplayer.parser.provider;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.hustunique.parsingplayer.parser.entity.IVideoInfo;
import com.hustunique.parsingplayer.parser.entity.Quality;
import com.hustunique.parsingplayer.parser.entity.VideoInfoImpl;
import com.hustunique.parsingplayer.player.io.LoadingCallback;
import com.hustunique.parsingplayer.player.io.ParsingFileManager;
import com.hustunique.parsingplayer.util.LogUtil;
import com.hustunique.parsingplayer.util.Util;

import java.lang.ref.WeakReference;

import static com.hustunique.parsingplayer.parser.entity.VideoInfoImpl.HD_HIGH;
import static com.hustunique.parsingplayer.parser.entity.VideoInfoImpl.HD_LOW;
import static com.hustunique.parsingplayer.parser.entity.VideoInfoImpl.HD_MEDIUM;
import static com.hustunique.parsingplayer.parser.entity.VideoInfoImpl.HD_STANDARD;

/**
 * Created by JianGuo on 2/10/17.
 * Implementation for concat protocol
 */

public class ConcatSourceProvider extends IVideoInfoProvider {
    private static final String TAG = "ConcatSourceProvider";
    private final ParsingFileManager mManager;
    private WeakReference<Context> mContext;
    private
    @Quality
    int mQuality;


    private String provideFileName() {
        return Uri.encode(getVideoInfo().getUri()) + "_" + getQuality();
    }

    @Override
    public void provideSource(@Quality int quality) {
        quality = quality == VideoInfoImpl.HD_UNSPECIFIED ? getHdByNetwork() : quality;
        mQuality = mVideoInfo.getBestHd(quality);
        LogUtil.i(TAG, "current quality:" + mQuality);
        String content = mVideoInfo.provideSource(mQuality);
        mManager.write(provideFileName(), content, new LoadingCallback<String>() {
            @Override
            public void onSuccess(String result) {
                mCallback.onProvided(result);
            }

            @Override
            public void onFailed(Exception e) {
                mCallback.onFail(e);
            }
        });
    }


    @Override
    public int getQuality() {
        return mQuality;
    }

    public ConcatSourceProvider(IVideoInfo videoInfo, Context context, Callback callback) {
        super(videoInfo, callback);
        mContext = new WeakReference<>(context);
        mManager = ParsingFileManager.getInstance(Util.getDiskCacheDir(context,
                Uri.encode(videoInfo.getUri())));
    }

    private
    @Quality
    int getHdByNetwork() {
        ConnectivityManager cm = (ConnectivityManager) mContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            LogUtil.e(TAG, "No networking found");
            makeToast();
            return VideoInfoImpl.HD_UNSPECIFIED;
        }
        switch (networkInfo.getType()) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_WIMAX:
            case ConnectivityManager.TYPE_ETHERNET:
                return HD_HIGH;
            case ConnectivityManager.TYPE_MOBILE:
                switch (networkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_LTE:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                        return HD_STANDARD;
                    case TelephonyManager.NETWORK_TYPE_UMTS: // 3G
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        return HD_MEDIUM;
                    case TelephonyManager.NETWORK_TYPE_GPRS: // 2G
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                        return HD_LOW;
                    default:
                        return HD_LOW;
                }
            default:
                return HD_LOW;
        }
    }

    private void makeToast() {
        Toast.makeText(mContext.get(), "No network,please check", Toast.LENGTH_SHORT).show();
    }
}
