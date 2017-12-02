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

package com.hustunique.parsingplayer.parser.entity;

import java.util.Set;

/**
 * Created by JianGuo on 5/13/17.
 * Interface for parsed video info from websites.
 */

public interface IVideoInfo {
    int HD_UNSPECIFIED = -1;
    int HD_LOW = 0;
    int HD_MEDIUM = 1;
    int HD_STANDARD = 2;
    int HD_HIGH = 3;
    String provideSource(@Quality int quality);
    String getTitle();
    Set<Integer> getQualities();
    String getUri();
    @Quality int getBestHd(@Quality int quality);
}
