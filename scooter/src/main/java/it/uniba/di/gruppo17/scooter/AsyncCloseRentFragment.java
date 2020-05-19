package it.uniba.di.gruppo17.scooter;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * @author Francesco Moramarco
 */
public class AsyncCloseRentFragment extends AsyncTask<URL, Void, Boolean> {

    private static final String TAG ="AsyncCloseRentFragment";

    @Override
    protected Boolean doInBackground(URL... urls) {

        boolean result = false;
        for (URL url : urls) {
            try {
                JSONObject j = JsonFromHttp.getJsonObject(url);
                if ( j != null )
                {
                    result = j.getBoolean("risultatoChiusura");
                    Log.d(TAG, "Chiusura noleggio: "+j.toString());
                }
                else
                {
                    Log.d(TAG,"JSONObject è null ");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG,"JSON Exception");
            }
        }
        return result;
    }
}
