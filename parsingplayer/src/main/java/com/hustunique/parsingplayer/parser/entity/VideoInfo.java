
package com.hustunique.parsingplayer.parser.entity;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Created by JianGuo on 1/16/17.
 * POJO for video information extracted from websites
 * A list of segs represents a steam
 */
// TODO: 1/16/17 define fields for information in videos
public class VideoInfo {
    /**
     * There are four qualities 0 ~4 for these formats.
     * <ul>
     * <li>3gp,flv,flvhd: 0</li>
     * <li>3gphd,mp4,mp4hd,mp4hd2,mp4hd3: 1</li>
     * <li>hd2: 2</li>
     * <li>hd3: 3</li>
     * </ul>
     */
    public static final String FORMAT_3GP = "3gp";
    public static final String FORMAT_3GPHD = "3gphd";
    public static final String FORMAT_FLV = "flv";
    public static final String FORMAT_FLVHD = "flvhd";
    public static final String FORMAT_MP4 = "mp4";
    public static final String FORMAT_MP4HD = "mp4hd";
    public static final String FORMAT_MP4HD2 = "mp4hd2";
    public static final String FORMAT_MP4HD3 = "mp4hd3";
    public static final String FORMAT_HD2 = "hd2";
    public static final String FORMAT_HD3 = "hd3";


    private Map<String, List<Seg>> segsMap;
    private String title;

    public List<Seg> getSegs(String format) {
        if (!segsMap.containsKey(format)) throw new RuntimeException("No such hd in this url");
        return segsMap.get(format);
    }

    public VideoInfo(@NonNull Map<String, List<Seg>> segsMap, @NonNull String title) {
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

