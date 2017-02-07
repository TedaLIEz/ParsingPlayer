package com.hustunique.parsingplayer.parser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Created by JianGuo on 1/17/17.
 * Unit test for {@link VideoParser}
 */
public class VideoParserTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void parseNullStringThrowException() {
        VideoParser videoParser = VideoParser.getInstance();
        thrown.expect(IllegalArgumentException.class);
        videoParser.createExtractor(null);
    }

    @Test
    public void parseUnsupportedURLThrowException() {
        VideoParser videoParser = VideoParser.getInstance();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("This url is not valid or unsupported yet");
        videoParser.parse(TestConstant.UNSUPPORTED_URL_1);
    }

    @Test
    public void parseInvalidURLThrowException() {
        VideoParser videoParser = VideoParser.getInstance();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("This url is not valid or unsupported yet");
        videoParser.parse("");
    }

    @Test
    public void parseValidURL() {
        VideoParser videoParser = VideoParser.getInstance();
        videoParser.parse(TestConstant.YOUKU_URL_1);
    }
}