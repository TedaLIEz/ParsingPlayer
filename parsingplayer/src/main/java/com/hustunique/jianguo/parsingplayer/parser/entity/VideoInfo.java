
package com.hustunique.jianguo.parsingplayer.parser.entity;

import java.util.List;
import java.util.Map;

/**
 * Created by JianGuo on 1/16/17.
 * POJO for video information extracted from websites
 */
// TODO: 1/16/17 define fields for information in videos
public class VideoInfo {

    // now four hds are enough
    public static int HD_0 = 0;
    public static int HD_1 = 1;
    public static int HD_2 = 2;
    public static int HD_3 = 3;

    private Map<Integer,List<Seg>> segsMap;
    private String title;

    public List<Seg> getSegs(int hd){
        if (!segsMap.containsKey(hd)) throw new RuntimeException("No such hd in this url");
        return segsMap.get(hd);
    }
    //TODO:construct this class and use HashMap or ?

}

