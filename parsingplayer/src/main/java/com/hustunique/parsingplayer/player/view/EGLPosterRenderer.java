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

package com.hustunique.parsingplayer.player.view;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.view.Surface;

public class EGLPosterRenderer {

    private Bitmap bitmap;
    private boolean recycleBitmap;

    private EGLPosterRenderer(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static EGLPosterRenderer render(Bitmap bitmap) {
        return new EGLPosterRenderer(bitmap);
    }

    public EGLPosterRenderer recycleBitmap(boolean recycleBitmap) {
        this.recycleBitmap = recycleBitmap;
        return this;
    }

    public void into(Surface surface) {
        new EGLPosterRendererThread(bitmap, recycleBitmap, surface).start();
    }

    public void into(SurfaceTexture surfaceTexture) {
        new EGLPosterRendererThread(bitmap, recycleBitmap, surfaceTexture).start();
    }
}