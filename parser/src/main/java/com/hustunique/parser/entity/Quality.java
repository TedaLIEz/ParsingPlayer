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

package com.hustunique.parser.entity;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by JianGuo on 2/10/17.
 * Integer range used in choosing quality
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({IVideoInfo.HD_UNSPECIFIED, IVideoInfo.HD_LOW, IVideoInfo.HD_MEDIUM, IVideoInfo.HD_STANDARD, IVideoInfo.HD_HIGH})
public @interface Quality {
}
