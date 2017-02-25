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
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hustunique.parsingplayer.player.media.MediaManagerTestHelper;
import com.hustunique.parsingplayer.player.view.ParsingVideoView;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.hustunique.parsingplayer.player.view.ParsingVideoViewAction.play;
import static com.hustunique.sample.TestUtil.URL_2;
import static com.hustunique.sample.TestUtil.download;
import static junit.framework.Assert.assertEquals;

/**
 * Created by JianGuo on 2/27/17.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> mRule = new ActivityTestRule<>(MainActivity.class);

    private Solo mSolo;



    @Before
    public void setUp() {
        mSolo = new Solo(InstrumentationRegistry.getInstrumentation(),
                mRule.getActivity());
    }

    @After
    public void tearDown() {
        mSolo.finishOpenedActivities();
    }

    @Test
    public void testWithVideoPlaying() {

        mSolo.unlockScreen();
        mSolo.assertCurrentActivity("Expected Current Activity", MainActivity.class);
        final ParsingVideoView videoView = (ParsingVideoView) mSolo.getView(R.id.videoView);
        String filePath = download(URL_2, URL_2.substring(URL_2.lastIndexOf('/')));

        if (filePath != null) {
            play(videoView, filePath);
            assertEquals(true, mSolo.waitForCondition(new Condition() {
                @Override
                public boolean isSatisfied() {
                    return MediaManagerTestHelper.isPlaying();
                }
            }, 10000));
        }

    }
}