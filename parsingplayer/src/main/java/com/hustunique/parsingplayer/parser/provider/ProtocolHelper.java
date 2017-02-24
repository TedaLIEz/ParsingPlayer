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

package com.hustunique.parsingplayer.parser.provider;

import com.hustunique.parsingplayer.parser.entity.Seg;

import java.util.List;

/**
 * Created by JianGuo on 1/29/17.
 * Used in ffmpeg protocols
 */

public class ProtocolHelper {

    /**
     * Create content of .concat file used in concat protocol
     * <br>
     * Example:
     * <pre>
         ffconcat version 1.0
         file http://www.example.com/foo
         duration 178.667
     * </pre>
     * @param segs list of segment, see {@link Seg}
     * @return String content of the .concat file
     */
    public static String concat(List<Seg> segs) {
        StringBuilder sb = new StringBuilder();
        sb.append("ffconcat version 1.0\n");
        for (Seg seg : segs) {
            sb.append("file ").append(seg.getPath()).append("\n");
            sb.append("duration ").append(seg.getDuration()).append("\n");
        }
        return sb.toString();
    }
}
