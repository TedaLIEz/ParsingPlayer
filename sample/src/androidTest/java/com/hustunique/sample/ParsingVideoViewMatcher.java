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

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;

import com.hustunique.parsingplayer.player.media.ParsingMediaManager;
import com.hustunique.parsingplayer.player.view.ParsingVideoView;

import org.hamcrest.Description;

/**
 * A Matcher for Espresso that checks status of {@link ParsingVideoView}
 */

public class ParsingVideoViewMatcher {
    public static BoundedMatcher<View, ParsingVideoView> isPlaying() {
        return new BoundedMatcher<View, ParsingVideoView>(ParsingVideoView.class) {
            @Override
            protected boolean matchesSafely(ParsingVideoView item) {
                return ParsingMediaManager.getInstance(InstrumentationRegistry.getTargetContext()).isPlaying();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("video is playing");
            }
        };
    }

}
