package com.hustunique.jianguo.parsingplayer.player;

import android.view.View;

/**
 * Created by JianGuo on 1/20/17.
 * Interface for a mediacontroller panel
 */

public interface IMediaController {
    void hide();

    boolean isShowing();

    void setAnchorView(View view);

    void setEnabled(boolean enabled);

    void setMediaPlayer(IMediaPlayerControl player);

    void show(int timeout);

    void show();

    //----------
    // Extends
    //----------
    void showOnce(View view);
}
