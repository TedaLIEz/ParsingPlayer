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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by JianGuo on 5/24/17.
 */

public class Util {
    public static String getMD5(String str) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(str.getBytes());
        String hashText = new BigInteger(1, md.digest()).toString(16);
        while (hashText.length() < 32) {
            hashText = "0" + hashText;
        }
        return hashText;
    }


    /**
     * RC4 encryption
     * Refer https://zh.wikipedia.org/wiki/RC4
     *
     * @param b1
     * @param b2
     * @return decoded byte array
     */
    public static byte[] rc4(byte[] b1, byte[] b2) {
        byte[] result = new byte[b2.length];

        int[] s = new int[256];
        for (int i = 0; i < 256; i++) {
            s[i] = i;
        }
        int t = 0;
        int tmp;
        for (int i = 0; i < 256; i++) {
            t = (t + s[i] + (b1[i % b1.length] & 0xff)) % 256;
            tmp = s[i];
            s[i] = s[t];
            s[t] = tmp;
        }
        int x = 0, y = 0;
        for (int i = 0; i < b2.length; i++) {
            y = (y + 1) % 256;
            x = (x + s[y]) % 256;
            tmp = s[x];
            s[x] = s[y];
            s[y] = tmp;
            result[i] = (byte) ((b2[i] & 0xff) ^ s[(s[x] + s[y]) % 256]);
        }
        return result;
    }
}
