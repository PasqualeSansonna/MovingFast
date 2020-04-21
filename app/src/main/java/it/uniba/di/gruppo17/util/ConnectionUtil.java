package it.uniba.di.gruppo17.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionUtil {
    public static boolean checkInternetConn(Activity anActivity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) anActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
}
