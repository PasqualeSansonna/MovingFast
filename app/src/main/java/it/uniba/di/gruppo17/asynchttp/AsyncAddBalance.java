package it.uniba.di.gruppo17.asynchttp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import it.uniba.di.gruppo17.PayFragment;

public class AsyncAddBalance extends AsyncTask<URL, Integer, Boolean> {
    @Override
    protected Boolean doInBackground(URL... params) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Boolean ok = false;
        for (URL param : params) {
            try {
                JSONObject j = JsonFromHttp.getJsonObject(param);
                ok = j.getBoolean("ok");
                Log.d("JSON", j.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ok;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        PayFragment.afterTask(aBoolean);
    }

}
