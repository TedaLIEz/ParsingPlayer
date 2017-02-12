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

/**
 * Created by CoXier on 17-1-16.
 * This class represents one part of one video stream.
 */

public class Seg {
    private String path;
    private double duration;

    public Seg(String path, double duration) {
        this.path = path;
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Seg seg = (Seg) o;

        if (duration != seg.duration) return false;
        return path.equals(seg.path);

    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = (int) (31 * result + duration);
        return result;
    }

    @Override
    public String toString() {
        return "Seg[path=" + path +
                ", duration=" + duration +
                ']';
    }
}
