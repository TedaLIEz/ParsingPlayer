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


import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.hustunique.sample.TestUtil.childAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public MainActivityTestRule rule = new MainActivityTestRule(MainActivity.class);


    @Test
    public void videoViewIsShowing() {
        ViewInteraction relativeLayout = onView(
                withId(R.id.videoView));
        relativeLayout.check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void controllerViewHideWhenStart() {
        onView(allOf(withId(R.id.controller_view),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.videoView),
                                        0),
                                1))).check(matches(not(isDisplayed())));
    }

    @Test
    public void controllerViewShowWhenClick() {
        ViewInteraction videoView = onView(
                withId(R.id.videoView));
        videoView.perform(click());
        ViewInteraction linearLayout = onView(
                withId(R.id.controller_view));
        linearLayout.check(matches(isDisplayed()));
    }


}
