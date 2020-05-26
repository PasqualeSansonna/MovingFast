package it.uniba.di.gruppo17.asynchttp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import it.uniba.di.gruppo17.WalletFragment;

public class AsyncGetDiscount extends AsyncTask<URL, Integer, Integer>
{
    @Override
    protected Integer doInBackground(URL... params) {
        int sconto = -1;
        for (URL param : params) {
            try {
                JSONObject j = JsonFromHttp.getJsonObject(param);
                sconto =  j.getInt("sconto");
                Log.d("JSON", j.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return sconto;
    }


}
