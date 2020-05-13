package it.uniba.di.gruppo17.scooter;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import java.net.URL;

/**
 * @author Francesco Moramarco
 */
public class AsyncCheckMonopattino extends AsyncTask<URL, Void, Boolean> {

    private static final String TAG ="AsyncCheckMonopattino";

    @Override
    protected Boolean doInBackground(URL... params) {

        boolean result = false;
        for (URL param : params) {
            JSONObject json = new JsonFromHttp().getJsonObject(param);
            try {
                if ( json.getString("id").equals("null") )
                    result = false;
                else
                    result = true;
            } catch (Exception e) {
                result = false;
                Log.d (TAG,"JSON Exception sollevata");
            }
        }
        return result;
    }
}