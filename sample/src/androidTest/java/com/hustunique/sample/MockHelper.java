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

package com.hustunique.sample;

import com.hustunique.parsingplayer.parser.entity.Seg;
import com.hustunique.parsingplayer.parser.entity.Stream;
import com.hustunique.parsingplayer.parser.entity.VideoInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JianGuo on 2/24/17.
 */

public class MockHelper {
    public static VideoInfo mockSimpleInfo(String title, String url, int duration) {
        Map<Integer, Stream> map = new HashMap<>();
        List<Seg> list = new ArrayList<>();
        list.add(new Seg(url, duration));
        Stream stream = new Stream(list);
        map.put(VideoInfo.HD_STANDARD, stream);
        return new VideoInfo(map, title);
    }

    public static VideoInfo mockQualityInfo(String title, String url, int duration) {
        Map<Integer, Stream> map = new HashMap<>();
        List<Seg> list = new ArrayList<>();
        list.add(new Seg(url, duration));
        Stream stream = new Stream(list);
        map.put(VideoInfo.HD_STANDARD, stream);
        list = new ArrayList<>();
        list.add(new Seg(url, duration));
        stream = new Stream(list);
        map.put(VideoInfo.HD_HIGH, stream);
        return new VideoInfo(map, title);

    }
}
