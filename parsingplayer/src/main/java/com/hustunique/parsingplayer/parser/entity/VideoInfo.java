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

import java.util.List;
import java.util.Map;

/**
 * Created by JianGuo on 1/16/17.
 * POJO for video information extracted from websites
 * A list of segs represents a steam
 */
public class VideoInfo {
    public static final int HD_0 = 0;
    public static final int HD_1 = 1;
    public static final int HD_2 = 2;
    public static final int HD_3 = 3;


    // the key is hd
    private Map<Integer, List<Seg>> segsMap;
    private String title;


    public List<Seg> getSegs(@Quality int hd) {
        while (!segsMap.containsKey(hd)){
            hd--;
            if (hd < 0)
                throw new RuntimeException("No such hd in this url");
        }
        return segsMap.get(hd);
    }

    public VideoInfo(@NonNull Map<Integer, List<Seg>> segsMap, @NonNull String title) {
        if (segsMap == null) throw new IllegalArgumentException("SegsMap can't be null");
        if (title == null) throw new IllegalArgumentException("Title can't be null");
        this.segsMap = segsMap;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "segsMap=" + segsMap +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof VideoInfo){
            VideoInfo anotherInfo = (VideoInfo) o;
            return anotherInfo.segsMap.equals(segsMap) && anotherInfo.title.equals(title);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = segsMap != null ? segsMap.hashCode() : 0;
        result = 31 * result + title.hashCode();
        return result;
    }
}

