package com.hustunique.jianguo.parsingplayer.parser;

import com.hustunique.jianguo.parsingplayer.TestConstant;

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
        VideoParser videoParser = new VideoParser();
        thrown.expect(IllegalArgumentException.class);
        videoParser.createExtractor(null);
    }

    @Test
    public void parseUnsupportedURLThrowException() {
        VideoParser videoParser = new VideoParser();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("This url is not valid or unsupported yet");
        videoParser.parse(TestConstant.UNSUPPORTED_URL_1);
    }

    @Test
    public void parseInvalidURLThrowException() {
        VideoParser videoParser = new VideoParser();
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("This url is not valid or unsupported yet");
        videoParser.parse("");
    }
}