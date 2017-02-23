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

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.hustunique.sample.ParsingVideoViewAction.play;
import static com.hustunique.sample.ParsingVideoViewMatcher.isPlaying;
import static com.hustunique.sample.TestUtil.YOUKU_URL_1;
import static org.hamcrest.Matchers.not;

/**
 * Created by JianGuo on 2/21/17.
 * UI test for {@link com.hustunique.parsingplayer.player.view.ParsingVideoView}
 * while playing video
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class VideoViewPlayingTest {
    private static final String TAG = "VideoViewTest";
    @Rule
    public ActivityTestRule<MainActivity> mRule = new ActivityTestRule<>(MainActivity.class);

    private MediaManagerIdleResource mResource;
    private ViewInteraction mVideoView;
    @Before
    public void setUp() {
        mResource = new MediaManagerIdleResource(mRule.getActivity());
        IdlingPolicies.setIdlingResourceTimeout(30, TimeUnit.SECONDS);
        mVideoView = onView(withId(R.id.videoView));
        mVideoView.perform(play(YOUKU_URL_1));
        Espresso.registerIdlingResources(mResource);
    }

    @After
    public void tearDown() {
        Espresso.unregisterIdlingResources(mResource);
    }


    @Test
    public void testWithVideoPlaying() {
        ViewInteraction linearLayout = onView(
                withId(R.id.controller_view));
        linearLayout.check(matches(not(isDisplayed())));
        mVideoView.perform(click());
        linearLayout.check(matches(isDisplayed()));
        ViewInteraction pauseButton = onView(
                withId(R.id.pause));
        pauseButton.check(matches(isCompletelyDisplayed()));
        ViewInteraction fullscreenButton = onView(
                withId(R.id.fullscreen));
        fullscreenButton.check(matches(isCompletelyDisplayed()));
        pauseButton.perform(click());
        mVideoView.check(matches(not(isPlaying())));
    }

}



