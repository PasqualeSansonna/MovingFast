package it.uniba.di.gruppo17.asynchttp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import it.uniba.di.gruppo17.AsyncResponse;
import it.uniba.di.gruppo17.R;
import it.uniba.di.gruppo17.RentFragment;

public class AsyncGetRentId extends AsyncTask<URL, Integer, Integer[]> {

    private AsyncResponse listener;

    public AsyncGetRentId(AsyncResponse listener)
    {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {

        super.onPreExecute();
    }

    @Override
    protected Integer[] doInBackground(URL... params) {

        //Per dare il tempo al monopattino di caricare sul server il noleggio. Non è necessario ma è per sicurezza
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Integer[] array = {-1, -1};
        for (URL param : params) {
            try {
                JSONObject j = JsonFromHttp.getJsonObject(param);
                array[0] = j.getInt("idRent");
                array[1] = j.getInt("idScooter");
                Log.d("JSON", j.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    @Override
    protected void onPostExecute(Integer[] integers) {
        super.onPostExecute(integers);
        listener.getResponse(integers);
    }
}