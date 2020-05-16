package it.uniba.di.gruppo17.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Pasquale
 * Controllo della connessione
 */
public class ConnectionUtil {
    public static boolean checkInternetConn(Activity anActivity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) anActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if ( activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting() )
            return true;
        else
            return false;
    }
}
