package com.hustunique.jianguo.parsingplayer.parser.entity;

/**
 * Created by CoXier on 17-1-16.
 * This class represents one part of one video stream.
 */

public class Seg {
    private String path;
    private long duration;

    public Seg(String path, long duration) {
        this.path = path;
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
