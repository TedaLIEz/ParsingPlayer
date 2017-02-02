package com.hustunique.parsingplayer.parser;

import com.google.gson.JsonObject;
import com.hustunique.parsingplayer.parser.extractor.Extractor;
import com.hustunique.parsingplayer.parser.extractor.YoukuExtractor;

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
