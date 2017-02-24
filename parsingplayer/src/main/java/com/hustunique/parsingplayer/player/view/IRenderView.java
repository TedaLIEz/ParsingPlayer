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

package com.hustunique.parsingplayer.player.view;

import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;

/**
 * Created by JianGuo on 1/21/17.
 */
public interface IRenderView {
    int AR_ASPECT_EXACTLY = -1;
    int AR_ASPECT_FIT_PARENT = 0; // without clip
    int AR_ASPECT_FILL_PARENT = 1; // may clip
    int AR_ASPECT_WRAP_CONTENT = 2;
    int AR_MATCH_PARENT = 3;
    int AR_16_9_FIT_PARENT = 4;
    int AR_4_3_FIT_PARENT = 5;


    boolean shouldWaitForResize();

    void setVideoSize(int videoWidth, int videoHeight);

    void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen);

    void setVideoRotation(int degree);

    void setAspectRatioMode(int aspectRatioMode);

    void setAspectRatio(float aspectRatio);

    void setRenderCallback(IRenderCallback callback);


    interface IRenderCallback {
        /**
         * @param surfaceTexture
         * @param width  could be 0
         * @param height could be 0
         */
        void onSurfaceCreated(@NonNull SurfaceTexture surfaceTexture, int width, int height);

        /**
         * @param surfaceTexture
         * @param format could be 0
         * @param width
         * @param height
         */
        void onSurfaceChanged(@NonNull SurfaceTexture surfaceTexture, int format, int width, int height);

        void onSurfaceDestroyed(@NonNull SurfaceTexture surfaceTexture);
    }
}
