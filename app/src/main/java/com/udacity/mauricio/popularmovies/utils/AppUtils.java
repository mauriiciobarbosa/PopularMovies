package com.udacity.mauricio.popularmovies.utils;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by mauricio-MTM on 12/6/2016.
 */

public final class AppUtils {

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int getDarketColor(int toolbarColor) {
        float[] hsv = new float[3];
        int color =toolbarColor;
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f; // value component
        color = Color.HSVToColor(hsv);
        return color;
    }

}
