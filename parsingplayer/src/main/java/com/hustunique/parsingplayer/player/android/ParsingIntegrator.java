/*
 * Copyright (c) 2017 UniqueStudio
 *
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

package com.hustunique.parsingplayer.player.android;

import android.content.Context;
import android.content.Intent;

/**
 * Created by JianGuo on 2/15/17.
 * Integrator for playing video in fullscreen
 */
public class ParsingIntegrator {
    private static final String TAG = "ParsingIntegrator";
    private Context mContext;

    public void parsingToPlay() {
        Intent intent = new Intent(mContext, VideoActivity.class);
        mContext.startActivity(intent);
    }

    public ParsingIntegrator(Context context) {
        mContext = context;
    }


}
