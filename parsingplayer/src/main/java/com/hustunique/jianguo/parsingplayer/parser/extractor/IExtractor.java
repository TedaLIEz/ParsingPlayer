

package com.hustunique.jianguo.parsingplayer.parser.extractor;

import android.support.annotation.NonNull;

import com.hustunique.jianguo.parsingplayer.parser.VideoParser;

/**
 * Created by JianGuo on 1/16/17.
 * Interface for extracting video info from websites
 */

public interface IExtractor {
    void extract(@NonNull String url, VideoParser.ExtractCallback callback);
}
