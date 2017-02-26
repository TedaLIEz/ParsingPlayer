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

package com.hustunique.parsingplayer.parser;import java.io.IOException;

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
