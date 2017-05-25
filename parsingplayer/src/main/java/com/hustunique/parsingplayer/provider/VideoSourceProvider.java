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

package com.hustunique.parsingplayer.provider;


import com.hustunique.parser.entity.IVideoInfo;
import com.hustunique.parser.entity.Quality;

/**
 * Created by JianGuo on 1/29/17.
 * Interface providing video source by {@link Quality}
 */
interface VideoSourceProvider {
    /**
     * return video data source by quality
     * @param quality integer specified in {@link IVideoInfo#HD_UNSPECIFIED},
     *                {@link IVideoInfo#HD_LOW}, {@link IVideoInfo#HD_MEDIUM},
     *                {@link IVideoInfo#HD_STANDARD}, {@link IVideoInfo#HD_HIGH}
     */
    void provideSource(@Quality int quality);

    @Quality int getQuality();
}
