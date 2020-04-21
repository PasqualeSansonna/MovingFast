package it.uniba.di.gruppo17.asynhttp;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class AsyncLogin extends AsyncTask<URL, String, Integer> {

    @Override
    protected Integer doInBackground(URL... params) {
        int result = -1;
        for (URL param : params) {
            JSONObject json = new JsonFromHttp().getJsonObject(param);
            try {
                if (!json.getString("id").equals("null") && !json.getString("nome").equals("null"))
                    result = json.getInt("id");
            } catch (JSONException e) {
                result = -1;
            }
        }
        return result;
    }
}
