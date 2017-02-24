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

package com.hustunique.parsingplayer.player.view;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import com.hustunique.parsingplayer.player.media.MediaManagerTestHelper;

import org.hamcrest.Matcher;

import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

/**
 * Created by JianGuo on 2/21/17.
 * View Action used in testing {@link ParsingVideoView}
 */

public class ParsingVideoViewAction {

    public static ViewAction play(final String url, final int duration, final String title) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ParsingVideoView.class);
            }

            @Override
            public String getDescription() {
                return "Play url " + url;
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((ParsingVideoView) view).setUrl(url);
                MediaManagerTestHelper.setVideoInfo(view.getContext(), url, duration ,title);
            }
        };
    }

    public static ViewAction play(final String url) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ParsingVideoView.class);
            }

            @Override
            public String getDescription() {
                return "Play url " + url;
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((ParsingVideoView) view).play(url);
            }
        };
    }

    public static ViewAction stop() {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ParsingVideoView.class);
            }

            @Override
            public String getDescription() {
                return "stop playing";
            }

            @Override
            public void perform(UiController uiController, View view) {
                ((ParsingVideoView) view).onPause();
            }
        };
    }
}
