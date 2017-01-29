package com.hustunique.parsingplayer.parser.entity;

/**
 * Created by CoXier on 17-1-16.
 * This class represents one part of one video stream.
 */

public class Seg {
    private String path;
    private int duration;

    public Seg(String path, int duration) {
        this.path = path;
        this.duration = duration;
    }

    public int getDuration() {
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
        result = 31 * result + duration;
        return result;
    }

    @Override
    public String toString() {
        return "Seg[path=" + path +
                ", duration=" + duration +
                ']';
    }
}
