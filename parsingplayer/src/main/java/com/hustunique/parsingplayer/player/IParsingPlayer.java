package com.hustunique.parsingplayer.player;

import android.support.annotation.IntDef;

import com.hustunique.parsingplayer.player.io.LoadingCallback;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by JianGuo on 2/2/17.
 */

public interface IParsingPlayer extends IMediaPlayer {
    /**
     * This will restrict the annotated param into  integers defined in the {@link IntDef} range
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IjkMediaPlayer.OPT_CATEGORY_CODEC, IjkMediaPlayer.OPT_CATEGORY_FORMAT,
            IjkMediaPlayer.OPT_CATEGORY_PLAYER, IjkMediaPlayer.OPT_CATEGORY_SWS})
    @interface OptionCategory {
    }

    void setConcatVideoPath(String concatVideoPath, String content, LoadingCallback<String> callback);
    void setOption(@OptionCategory int category, String name, String value);
    void setOption(@OptionCategory int category, String name, long value);
}
