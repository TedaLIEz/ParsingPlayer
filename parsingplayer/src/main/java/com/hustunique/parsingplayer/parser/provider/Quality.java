package com.hustunique.parsingplayer.parser.provider;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.hustunique.parsingplayer.parser.entity.VideoInfo.HD_0;
import static com.hustunique.parsingplayer.parser.entity.VideoInfo.HD_1;
import static com.hustunique.parsingplayer.parser.entity.VideoInfo.HD_2;
import static com.hustunique.parsingplayer.parser.entity.VideoInfo.HD_3;
import static com.hustunique.parsingplayer.parser.entity.VideoInfo.HD_UNSPECIFIED;

/**
 * Created by JianGuo on 2/10/17.
 * Integer range used in choosing quality
 */

@Retention(RetentionPolicy.SOURCE)
@IntDef({HD_UNSPECIFIED, HD_0, HD_1, HD_2, HD_3})
public @interface Quality {
}
