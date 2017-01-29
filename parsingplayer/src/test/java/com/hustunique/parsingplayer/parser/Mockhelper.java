package com.hustunique.parsingplayer.parser;

import java.io.IOException;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.Okio;
import okio.Source;
import okio.Timeout;

/**
 * Created by JianGuo on 1/17/17.
 * helper for mocking objects used in unit test
 */

public class Mockhelper {
    public static Response mockResponse(String url, String responseStr) throws IOException {
        ResponseBody responseBody = newResponseBody(responseStr);
        return new Response.Builder()
                .request(new Request.Builder()
                .url(url)
                .build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .body(responseBody)
                .build();
    }
    private static ResponseBody newResponseBody(String responseStr) {
        final Buffer data = new Buffer().writeUtf8(responseStr);
        Source source = new Source() {
            boolean closed;
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                if (closed) throw new IllegalStateException();
                return data.read(sink, byteCount);
            }

            @Override
            public Timeout timeout() {
                return Timeout.NONE;
            }

            @Override
            public void close() throws IOException {
                closed = true;
            }
        };
        return ResponseBody.create(null, -1, Okio.buffer(source));
    }

}
