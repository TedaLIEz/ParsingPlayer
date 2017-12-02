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
import java.util.TreeSet;

/**
 * Created by JianGuo on 1/16/17.
 * POJO for video information extracted from websites
 * A list of segs represents a steam
 */
public class VideoInfoImpl implements IVideoInfo {


    // the key is hd
    private Map<Integer, Stream> mStreamMap;
    private String mTitle;

    // Each video has an unique id
    private String mId;
    private String mUri;

    public VideoInfoImpl(@NonNull String uri, @NonNull Map<Integer, Stream> streamMap,
                         @NonNull String title, @NonNull  String id) {
        if (id == null) throw new IllegalArgumentException("Id can't be null");
        if (streamMap == null) throw new IllegalArgumentException("SegsMap can't be null");
        if (title == null) throw new IllegalArgumentException("Title can't be null");
        this.mId = id;
        this.mUri = uri;
        this.mStreamMap = streamMap;
        this.mTitle = title;
    }

    @Override
    public String getTitle() {
        return mTitle.replaceAll(" ", "");
    }


    public Map<Integer, Stream> getStreamMap() {
        return mStreamMap;
    }

    @Override
    public String provideSource(@Quality int quality) {
        return ProtocolHelper.concat(getStream(quality).getSegs());
    }

    public String getId() {
        return mId;
    }

    @Override
    public Set<Integer> getQualities() {
        return new TreeSet<>(mStreamMap.keySet());
    }

    @Override
    public String getUri() {
        return mUri;
    }

    @Override
    public int getBestHd(@Quality int quality) {
        while (mStreamMap.get(quality) == null){
            quality -- ;
        }
        return quality;
    }

    private Stream getStream(@Quality int hd) {
        return mStreamMap.get(hd);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoInfoImpl videoInfo = (VideoInfoImpl) o;

        if (!mStreamMap.equals(videoInfo.mStreamMap)) return false;
        if (!mTitle.equals(videoInfo.mTitle)) return false;
        if (!mId.equals(videoInfo.mId)) return false;
        return mUri.equals(videoInfo.mUri);

    }

    @Override
    public int hashCode() {
        int result = mStreamMap.hashCode();
        result = 31 * result + mTitle.hashCode();
        result = 31 * result + mId.hashCode();
        result = 31 * result + mUri.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "VideoInfoImpl{" +
                "mStreamMap=" + mStreamMap +
                ", mTitle='" + mTitle + '\'' +
                ", mId='" + mId + '\'' +
                ", mUri='" + mUri + '\'' +
                '}';
    }
}

