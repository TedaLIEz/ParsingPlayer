package com.hustunique.jianguo.parsingplayer.parser.extractor;

import com.google.gson.JsonObject;

/**
 * Created by JianGuo on 1/25/17.
 * Exception used in {@link Extractor}, see {@link YoukuExtractor#checkError(JsonObject)}
 */

public class ExtractException extends IllegalStateException {
    public ExtractException() {
        super();
    }
    public ExtractException(String s) {
        super(s);
    }
}
