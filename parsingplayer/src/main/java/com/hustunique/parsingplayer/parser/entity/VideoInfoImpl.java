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

import com.hustunique.parsingplayer.parser.provider.ProtocolHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by JianGuo on 1/16/17.
 * POJO for video information extracted from websites
 * A list of segs represents a steam
 */
public class VideoInfoImpl implements VideoInfo {


    // the key is hd
    private Map<Integer, Stream> streamMap;
    private String title;

    // Each video has an unique id
    private String id;


    public VideoInfoImpl(@NonNull String id, @NonNull Map<Integer, Stream> streamMap, @NonNull String title) {
        if (id == null) throw new IllegalArgumentException("Id can't be null");
        if (streamMap == null) throw new IllegalArgumentException("SegsMap can't be null");
        if (title == null) throw new IllegalArgumentException("Title can't be null");
        this.id = id;
        this.streamMap = streamMap;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }


    public Map<Integer, Stream> getStreamMap() {
        return streamMap;
    }

    @Override
    public String provideSource(@Quality int quality) {
        while (getStream(quality) == null) {
            quality--;
        }
        if (quality < HD_LOW) {
            throw new RuntimeException("No such hd in this url");
        }
        return ProtocolHelper.concat(getStream(quality).getSegs());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Set<Integer> getQualities() {
        return streamMap.keySet();
    }

    private Stream getStream(@Quality int hd) {
        return streamMap.get(hd);
    }

    @Override
    public String toString() {
        return "VideoInfoImpl{" +
                "segsMap=" + streamMap +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        int result = streamMap != null ? streamMap.hashCode() : 0;
        result = 31 * result + title.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof VideoInfoImpl) {
            VideoInfoImpl anotherInfo = (VideoInfoImpl) o;
            return anotherInfo.streamMap.equals(streamMap) && anotherInfo.title.equals(title);
        }
        return false;
    }
}

