package it.uniba.di.gruppo17.asynchttp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import it.uniba.di.gruppo17.util.Keys;

/**
 * @author Andrea
 * Classe usata in login e signup per verificare la risposta del server
 */
public class AsyncCheckConnection extends AsyncTask <Void,Void,Boolean>{

    private Context mContext;
    public AsyncCheckConnection(Context context)
    {
        this.mContext = context;
    }

    private boolean isConnectionAvailable(Context context)
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if ( activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting() )
            return true;
        else
            return false;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean success = false;
        if ( isConnectionAvailable( this.mContext ) )
        {
            try {
                URL url = new URL(Keys.SERVER);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(Keys.TIMEOUT);
                connection.connect();
                success = connection.getResponseCode() == 200;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
}
