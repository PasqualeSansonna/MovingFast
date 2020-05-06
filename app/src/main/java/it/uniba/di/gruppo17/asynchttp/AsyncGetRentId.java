package it.uniba.di.gruppo17.asynchttp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class AsyncGetRentId extends AsyncTask<URL, Integer, Integer[]> {


    @Override
    protected Integer[] doInBackground(URL... params) {

        Integer[] array = {-1, -1};
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (URL param : params) {
            try {
                JSONObject j = JsonFromHttp.getJsonObject(param);
                array[0] = j.getInt("id");
                array[1] = j.getInt("id_scooter");
                Log.d("JSON", j.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer[] integers) {
        super.onPostExecute(integers);
       // ResultFragment.afterTask(integers); NB: ho sistemato usando il metodo get di execute
    }
}