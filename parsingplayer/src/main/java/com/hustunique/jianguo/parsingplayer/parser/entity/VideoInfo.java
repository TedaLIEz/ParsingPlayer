
package com.hustunique.jianguo.parsingplayer.parser.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by JianGuo on 1/16/17.
 * POJO for video information extracted from websites
 * A list of segs represents a steam
 */
// TODO: 1/16/17 define fields for information in videos
public class VideoInfo {

    public static String FORMAT_3GP = "3gp";
    public static String FORMAT_3GPHD = "3gphd";
    public static String FORMAT_FLV = "flv";
    public static String FORMAT_FLVHD = "flvhd";
    public static String FORMAT_MP4 = "mp4";
    public static String FORMAT_MP4HD = "mp4hd";
    public static String FORMAT_MP4HD2 = "mp4hd2";
    public static String FORMAT_MP4HD3 = "mp4hd3";
    public static String FORMAT_HD2 = "hd2";
    public static String FORMAT_HD3 = "hd3";


    private Map<String,List<Seg>> segsMap;
    private String title;

    public List<Seg> getSegs(String format){
        if (!segsMap.containsKey(format)) throw new RuntimeException("No such hd in this url");
        return segsMap.get(format);
    }

    public VideoInfo(Map<String, List<Seg>> segsMap, String title) {
        this.segsMap = segsMap;
        this.title = title;
    }
}

