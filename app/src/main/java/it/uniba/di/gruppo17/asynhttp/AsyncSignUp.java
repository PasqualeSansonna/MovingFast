package it.uniba.di.gruppo17.asynhttp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class AsyncSignUp extends AsyncTask<URL, String, Boolean> {
    @Override
    protected Boolean doInBackground(URL... params) {

        boolean result = false;
        for (URL param : params) {
            JSONObject json = new JsonFromHttp().getJsonObject(param);
            try {
                if (json.getString("id") != null)
                    result = true;
            } catch (Exception e) {
                result = false;
            }
        }
        return result;
    }
}
