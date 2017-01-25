package com.hustunique.jianguo.parsingplayer.parser.entity;

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
}
