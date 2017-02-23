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

package com.hustunique.parsingplayer.parser.entity;

import android.support.annotation.NonNull;

import com.hustunique.parsingplayer.parser.provider.Quality;

import java.util.Map;

/**
 * Created by JianGuo on 1/16/17.
 * POJO for video information extracted from websites
 * A list of segs represents a steam
 */
public class VideoInfo {
    public static final int HD_UNSPECIFIED = -1;
    public static final int HD_LOW = 0;
    public static final int HD_MEDIUM = 1;
    public static final int HD_STANDARD = 2;
    public static final int HD_HIGH = 3;


    // the key is hd
    private Map<Integer, Stream> streamMap;
    private String title;


    public Stream getStream(@Quality int hd) {
        return streamMap.get(hd);
    }

    public VideoInfo(@NonNull Map<Integer, Stream> streamMap, @NonNull String title) {
        if (streamMap == null) throw new IllegalArgumentException("SegsMap can't be null");
        if (title == null) throw new IllegalArgumentException("Title can't be null");
        this.streamMap = streamMap;
        this.title = title;
    }



    public String getTitle() {
        return title;
    }

    public Map<Integer, Stream> getStreamMap() {
        return streamMap;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "segsMap=" + streamMap +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof VideoInfo) {
            VideoInfo anotherInfo = (VideoInfo) o;
            return anotherInfo.streamMap.equals(streamMap) && anotherInfo.title.equals(title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = streamMap != null ? streamMap.hashCode() : 0;
        result = 31 * result + title.hashCode();
        return result;
    }
}

