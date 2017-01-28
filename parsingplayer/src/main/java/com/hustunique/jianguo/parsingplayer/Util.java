

package com.hustunique.jianguo.parsingplayer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by JianGuo on 1/16/17.
 * Util class
 */

public class Util {

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean checkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * Write <tt>data</tt> to the <tt>context</tt> application package
     * @param filename the filename
     * @param data the data to be written
     * @param context the context
     */
    public static void writeToFile(String filename, String data, Context context) {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(filename,
                    Context.MODE_PRIVATE));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read data from target filename saved privately in <tt>context</tt> application package
     * @param filename the file name
     * @param context context
     * @return Stream data
     */
    public static String readFromFile(String filename, Context context) {
        return context.getFileStreamPath(filename).getAbsolutePath();
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
