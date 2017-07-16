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

package com.hustunique.parsingplayer.parser.extractor;

import android.os.Build;

import com.hustunique.parsingplayer.parser.ExtractException;
import com.hustunique.parsingplayer.parser.Mockhelper;
import com.hustunique.parsingplayer.parser.TestConstant;
import com.hustunique.parsingplayer.parser.entity.VideoInfoImpl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.Response;

import static junit.framework.Assert.assertEquals;


/**
 * Created by JianGuo on 1/17/17.
 * Unit test for {@link YoukuExtractor}
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.LOLLIPOP}, manifest = "src/main/AndroidManifest.xml")
public class YoukuExtractorTest {


    @Rule
    public ExpectedException mExpectedException = ExpectedException.none();


    @Test
    public void createInfoWithEmptyResponseBodyWillFail() throws IOException {
        YoukuExtractor youkuExtractor = new YoukuExtractor();
        Response response = Mockhelper.mockResponse(TestConstant.YOUKU_URL_1, "");
        mExpectedException.expect(IllegalStateException.class);
        youkuExtractor.createInfo(response);
    }


    @Test
    public void buildRequestWithNullURLWillThrowException() {
        YoukuExtractor youkuExtractor = new YoukuExtractor();
        mExpectedException.expect(IllegalArgumentException.class);
        youkuExtractor.buildRequest(null);
    }



    @Test
    public void checkErrorInCreateInfo() throws IOException {
        YoukuExtractor youkuExtractor = new YoukuExtractor();
        mExpectedException.expect(ExtractException.class);
        mExpectedException.expectMessage("Youku said: Sorry, this video is private");
        VideoInfoImpl videoInfoImpl = youkuExtractor.createInfo(Mockhelper.
                mockResponse(TestConstant.YOUKU_ERROR_URL_1,
                        TestConstant.YOUKU_ERROR_JSON_1));
        assertEquals(null, videoInfoImpl);
    }

}