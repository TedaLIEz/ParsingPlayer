/*
 *
 * Copyright (c) 2017 UniqueStudio
 *
 * This file is part of ParsingPlayer.
 *
 * ParsingPlayer is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ParsingPlayer; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.hustunique.parsingplayer.player.view;

/**
 * Created by JianGuo on 1/20/17.
 * Interface for a mediacontroller panel
 */
@Deprecated
public interface IMediaController {
    void hide();

    void complete();

    boolean isShowing();

    void setEnabled(boolean enabled);

    void setMediaPlayer(IMediaPlayerControl player);

    void show();

}
