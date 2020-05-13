package it.uniba.di.gruppo17.scooter;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * @author Francesco Moramarco
 */
public class AsyncRentScooter extends AsyncTask<URL, Integer, Integer> {

    private static final String TAG ="AsyncRentScooter";

    @Override
    protected Integer doInBackground(URL... urls) {
        int id = -1;
        for (URL url : urls) {
            try {
                JSONObject j = JsonFromHttp.getJsonObject(url);
                if ( j != null )
                {
                    id = j.getInt("idRent");
                    Log.d(TAG, "ID NOLEGGIO: "+j.toString());
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
        return id;
    }
}