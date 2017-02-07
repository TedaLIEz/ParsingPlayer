package com.hustunique.parsingplayer.parser.entity;

import java.util.List;

/**
 * Created by JianGuo on 1/29/17.
 * Used in ffmpeg protocols
 */

public class ProtocolHelper {

    /**
     * Create content of *.concat file used in concat protocol
     * <br>
     * Example:
     * <pre>
     *     ffconcat version 1.0
     *     file http://k.youku.com/player/getFlvPath/sid/048455411693812bdd99a_00/st/flv/fileid/030001010058774A2CB9A9059E49FE220E3CC1-C869-0180-6843-BF9932E316E8?ypp=0&myp=0&K=afa53a85a7e5c378282c0be9%26sign%3D253e61392b8eff662013212ab183c891&ctype=12&token=0544&ev=1&ep=ciacHkyIUs8B4yrcgD8bNXi2fX5eXP4J9h%2BFgNJjALshQO%2B4nU%2FTtO%2B5P%2FZCE%2FBsditwZenzq6XkGTMVYYNLr2EQ30%2BgOfrm9vTg5d8lzZkDZGw1c8uivFSeRjT1&hd=1&oip=1939659569
     *     duration 178.667
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
