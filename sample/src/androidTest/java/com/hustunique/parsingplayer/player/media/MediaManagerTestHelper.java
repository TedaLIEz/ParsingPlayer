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

package com.hustunique.parsingplayer.player.media;

import android.support.test.InstrumentationRegistry;


/**
 * Created by JianGuo on 2/24/17.
 */

public class MediaManagerTestHelper {

    public static void setVideoUri(String uri) {
        ParsingMediaManager.getInstance(InstrumentationRegistry.getTargetContext()).playOrigin(uri);
    }

    public static boolean isPlaying() {
        return ParsingMediaManager.getInstance(InstrumentationRegistry.getTargetContext()).isPlaying();
    }
}
