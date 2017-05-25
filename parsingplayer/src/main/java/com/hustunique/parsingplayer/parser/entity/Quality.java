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

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hustunique.parsingplayer.parser.entity.VideoInfoImpl.HD_LOW;
import static com.hustunique.parsingplayer.parser.entity.VideoInfoImpl.HD_MEDIUM;
import static com.hustunique.parsingplayer.parser.entity.VideoInfoImpl.HD_STANDARD;
import static com.hustunique.parsingplayer.parser.entity.VideoInfoImpl.HD_HIGH;
import static com.hustunique.parsingplayer.parser.entity.VideoInfoImpl.HD_UNSPECIFIED;

/**
 * Created by JianGuo on 2/10/17.
 * Integer range used in choosing quality
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({HD_UNSPECIFIED, HD_LOW, HD_MEDIUM, HD_STANDARD, HD_HIGH})
public @interface Quality {
}
