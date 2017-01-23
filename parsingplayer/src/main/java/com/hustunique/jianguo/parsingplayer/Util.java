

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


}
