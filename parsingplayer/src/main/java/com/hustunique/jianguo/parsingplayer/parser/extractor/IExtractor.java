

package com.hustunique.jianguo.parsingplayer.parser.extractor;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hustunique.jianguo.parsingplayer.parser.entity.VideoInfo;

/**
 * Created by JianGuo on 1/16/17.
 * Interface for extracting video info from websites
 */

public interface IExtractor {
    @Nullable
    VideoInfo extract(@NonNull String url);
}
