package com.hustunique.parsingplayer.parser.extractor;

import android.os.Build;

import com.hustunique.parsingplayer.parser.ExtractException;
import com.hustunique.parsingplayer.parser.Mockhelper;
import com.hustunique.parsingplayer.parser.TestConstant;
import com.hustunique.parsingplayer.parser.entity.VideoInfo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
    public void constructBasicUrl() {
        YoukuExtractor youkuExtractor = new YoukuExtractor();
        String baseUrl = youkuExtractor.constructBasicUrl(TestConstant.YOUKU_URL_1);
        assertEquals("http://play.youku.com/play/get.json?vid=XMjQ3MzE1NDA3Ng&ct=12", baseUrl);
    }

    @Test
    public void constructBasicUrlWithNullURLWillThrowException() {
        YoukuExtractor youkuExtractor = new YoukuExtractor();
        mExpectedException.expect(IllegalArgumentException.class);
        youkuExtractor.constructBasicUrl(null);
    }

    @Test
    public void checkErrorInCreateInfo() throws IOException {
        YoukuExtractor youkuExtractor = new YoukuExtractor();
        mExpectedException.expect(ExtractException.class);
        mExpectedException.expectMessage("Youku said: Sorry, this video is private");
        VideoInfo videoInfo = youkuExtractor.createInfo(Mockhelper.
                mockResponse(TestConstant.YOUKU_ERROR_URL_1,
                        TestConstant.YOUKU_ERROR_JSON_1));
        assertEquals(null, videoInfo);
    }

    @Test
    public void EpGenerateTest() throws UnsupportedEncodingException {
        YoukuExtractor youkuExtractor = new YoukuExtractor();
        String ep = youkuExtractor.getEp(TestConstant.YOUKU_EP_INPUT1);
        assertEquals("ciacH0yFU8kE4SbXjj8bby7jciNcXP4J9h+HgdJjALshQO/M703RwpSy" +
                "So1AYPkfcSIAE+nyqtiSaUIQYfZHrR4Q2U+oPfrh+vCQ5a1Xx5QFbx9EA8XRx1SZRDL1",
                ep);
    }

    @Test
    public void GetSidAndTokenTest() {
        YoukuExtractor youkuExtractor = new YoukuExtractor();
        String[] rst = youkuExtractor.getSidAndToken(TestConstant.YOUKU_ENCRYPT_INPUT1);
        assertEquals(2, rst.length);
        assertEquals("0485585744586128216dc", rst[0]);
        assertEquals("7724", rst[1]);
    }







}