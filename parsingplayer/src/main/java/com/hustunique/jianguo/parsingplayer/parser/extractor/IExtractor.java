

package com.hustunique.jianguo.parsingplayer.parser.extractor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hustunique.jianguo.parsingplayer.parser.VideoInfo;

/**
 * Created by JianGuo on 1/16/17.
 * Interface for extracting video info from websites
 */

public interface IExtractor {
    /**
     * return <tt>true</tt> if the host matches
     * @param host the host of url.
     * @return <tt>true</tt> if the host matches
     */
    boolean hostMatches(@NonNull String host);
    @Nullable
    VideoInfo extract(@NonNull String url);
}
