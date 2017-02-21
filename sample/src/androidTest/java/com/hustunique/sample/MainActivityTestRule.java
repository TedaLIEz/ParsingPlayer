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

import android.support.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static com.hustunique.sample.TestUtil.waitFor;

/**
 * Created by JianGuo on 2/21/17.
 * Custom test rule for UI test, we want to check video for a while.
 */

class MainActivityTestRule extends ActivityTestRule<MainActivity> {

    MainActivityTestRule(Class<MainActivity> activityClass) {
        this(activityClass, false);
    }

    MainActivityTestRule(Class<MainActivity> activityClass, boolean initialTouchMode) {
        this(activityClass, initialTouchMode, true);
    }

    MainActivityTestRule(Class<MainActivity> activityClass, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
    }

    @Override
    protected void afterActivityLaunched() {
        super.afterActivityLaunched();
        onView(isRoot()).perform(waitFor(5000));
    }
}
