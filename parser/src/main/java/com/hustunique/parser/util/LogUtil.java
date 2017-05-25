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

package com.hustunique.parser.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by JianGuo on 5/24/17.
 */

public class LogUtil {
    public static void d(String tag, String msg) {
        Logger.getLogger(tag).log(Level.CONFIG, msg);
    }

    public static void wtf(String tag, Exception e) {
        Logger.getLogger(tag).log(Level.SEVERE, e.getMessage());
    }


    public static void e(String tag, String msg) {
        Logger.getLogger(tag).log(Level.SEVERE, msg);
    }

}
