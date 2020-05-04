package it.uniba.di.gruppo17.asynchttp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import it.uniba.di.gruppo17.WalletFragment;

public class AsyncGetBalance extends AsyncTask<URL, Integer, Double>
{
    @Override
    protected Double doInBackground(URL... params) {
        double saldo = -1;
        for (URL param : params) {
            try {
                JSONObject j = JsonFromHttp.getJsonObject(param);
                saldo =  j.getDouble("saldo");
                Log.d("JSON", j.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return saldo;
    }

    public void onPostExecute(Double aDouble) {
        super.onPostExecute(aDouble);
        WalletFragment.afterTask(aDouble);
    }

}
