package com.hustunique.jianguo.parsingplayer.parser.extractor;

import com.hustunique.jianguo.parsingplayer.TestConstant;
import com.hustunique.jianguo.parsingplayer.parser.Mockhelper;
import com.hustunique.jianguo.parsingplayer.parser.entity.VideoInfo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import okhttp3.Response;

import static junit.framework.Assert.assertEquals;


/**
 * Created by JianGuo on 1/17/17.
 * Unit test for {@link YoukuExtractor}
 */
@RunWith(MockitoJUnitRunner.class)
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






}