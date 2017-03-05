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

package com.hustunique.parsingplayer.player.media;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by JianGuo on 2/2/17.
 * Interface extending {@link IMediaPlayer}
 */

interface IParsingPlayer extends IMediaPlayer {
    /**
     * This will restrict the annotated param into  integers defined in the {@link IntDef} range
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IjkMediaPlayer.OPT_CATEGORY_CODEC, IjkMediaPlayer.OPT_CATEGORY_FORMAT,
            IjkMediaPlayer.OPT_CATEGORY_PLAYER, IjkMediaPlayer.OPT_CATEGORY_SWS})
    @interface OptionCategory {
    }

    int PARSING_ERROR = -10002;
    int INVALID_VIDEO_INFO = -1;
//    void setConcatVideoPath(String concatVideoPath, String content, LoadingCallback<String> callback);
    void setOption(@OptionCategory int category, String name, String value);
    void setOption(@OptionCategory int category, String name, long value);
}
