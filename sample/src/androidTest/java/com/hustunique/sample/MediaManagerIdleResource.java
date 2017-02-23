/*
 * Copyright (c) 2017 UniqueStudio
 *
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

package com.hustunique.sample;

import android.content.Context;
import android.support.test.espresso.IdlingResource;

import com.hustunique.parsingplayer.player.media.ParsingMediaManager;
import com.hustunique.parsingplayer.player.view.ParsingVideoView;

import java.lang.ref.WeakReference;

/**
 * Created by JianGuo on 2/21/17.
 */

class MediaManagerIdleResource implements IdlingResource {
    private ResourceCallback mResourceCallback;
    private Context mContext;
    private WeakReference<ParsingVideoView> mVideoView;
    MediaManagerIdleResource(MainActivity activity) {
        mVideoView = new WeakReference<>((ParsingVideoView) activity.findViewById(R.id.videoView));
        mContext = activity;
    }

    @Override
    public String getName() {
        return "Wait for uri loaded " + mVideoView.get().toString();
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = !ParsingMediaManager.getInstance(mContext).isIdle();
        if (idle) {
            if (mResourceCallback != null) {
                mResourceCallback.onTransitionToIdle();
            }
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mResourceCallback = callback;
    }
}
