package com.hustunique.parsingplayer.parser.entity;

import java.util.List;

/**
 * Created by CoXier on 17-2-14.
 */

public class Stream {
    private List<Seg> segs;
    private int size;

    public Stream(List<Seg> segs) {
        this.segs = segs;
    }

    public List<Seg> getSegs() {
        return segs;
    }

    public void setSegs(List<Seg> segs) {
        this.segs = segs;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
