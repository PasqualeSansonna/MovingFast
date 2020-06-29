package it.uniba.di.gruppo17.scooter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Francesco Moramarco
 */
public class CheckInternetConnection extends AsyncTask<Void,Void,Boolean> {

    private Context mContext;
    public CheckInternetConnection(Context context)
    {
        this.mContext = context;
    }

    /*
        Controlla se la connessione dati o il wi-fi sono attivi
     */
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

    /*
        Metodo che consente di pingare il server per verificare se questo è disponibile
     */
    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean success = false;
        if ( isConnectionAvailable( this.mContext ) )
        {
            try {
                URL url = new URL(Keys.SERVER_HOME);
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