/*
 *
 * Copyright (c) 2017 UniqueStudio
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
