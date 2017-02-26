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

package com.hustunique.parsingplayer.parser;import android.os.Build;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;

/**
 * Created by JianGuo on 1/17/17.
 * Unit test for {@link VideoParser}
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.LOLLIPOP}, manifest = "src/main/AndroidManifest.xml")
public class VideoParserTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parseNullStringThrowException() {
        VideoParser videoParser = VideoParser.getInstance();
        thrown.expect(ExtractException.class);
        videoParser.createExtractor(null);
    }

    @Test
    public void parseUnsupportedURLReturnNull() {
        VideoParser videoParser = VideoParser.getInstance();
        assertEquals(null, videoParser.parse(TestConstant.UNSUPPORTED_URL_1));
    }

    @Test
    public void parseInvalidURLReturnNull() {
        VideoParser videoParser = VideoParser.getInstance();
        assertEquals(null, videoParser.parse(""));
    }

}