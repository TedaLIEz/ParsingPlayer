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
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.hustunique.parsingplayer.parser.entity.VideoInfo;
import com.hustunique.parsingplayer.util.LogUtil;

import static com.hustunique.parsingplayer.parser.entity.VideoInfo.HD_HIGH;
import static com.hustunique.parsingplayer.parser.entity.VideoInfo.HD_LOW;
import static com.hustunique.parsingplayer.parser.entity.VideoInfo.HD_MEDIUM;
import static com.hustunique.parsingplayer.parser.entity.VideoInfo.HD_STANDARD;

/**
 * Created by JianGuo on 2/10/17.
 * Implementation for concat protocol
 */

public class ConcatSourceProvider extends VideoInfoSourceProvider {
    private static final String TAG = "ConcatSourceProvider";
    private Context mContext;
    private
    @Quality
    int mQuality;
    @Override
    public String provideSource(@Quality int quality) {
        quality = quality == VideoInfo.HD_UNSPECIFIED ? getHdByNetwork() : quality;
        while (mVideoInfo.getStream(quality) == null) {
            quality--;
        }
        if (quality < VideoInfo.HD_LOW)
            throw new RuntimeException("No such hd in this url");
        mQuality = quality;
        LogUtil.i(TAG,"current quality:" + mQuality);
        return ProtocolHelper.concat(mVideoInfo.getStream(quality).getSegs());
    }

    @Override
    public int getQuality() {
        return mQuality;
    }

    public ConcatSourceProvider(VideoInfo videoInfo, Context context) {
        super(videoInfo);
        mContext = context;
    }

    private
    @Quality
    int getHdByNetwork() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo==null || !networkInfo.isConnected()){
            LogUtil.e(TAG, "No networking found");
            makeToast();
            return VideoInfo.HD_UNSPECIFIED;
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
        Toast.makeText(mContext,"No network,please check",Toast.LENGTH_SHORT).show();
    }
}
