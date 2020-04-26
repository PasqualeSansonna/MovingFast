package it.uniba.di.gruppo17.asynhttp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import it.uniba.di.gruppo17.util.Scooter;

public class AsyncGetScooters extends AsyncTask<URL, Void, Boolean> {

    private static final String TAG ="AsyncGetMonopattini";

    @Override
    protected Boolean doInBackground(URL... params) {

        boolean result = false;
        ArrayList<Scooter> nearScooters = null;
        JSONObject monopattini = JsonFromHttp.getJsonObject(params[0]);
        if ( monopattini !=null && monopattini.length()!=0 )
        {
            try {
                JSONObject idMonopattini = monopattini.getJSONObject("id");
                JSONObject latitutdineMonopattini = monopattini.getJSONObject("lat");
                JSONObject longitudineMonopattini = monopattini.getJSONObject("lon");
                JSONObject batteriaMonopattini = monopattini.getJSONObject("batteria");
                nearScooters = new ArrayList<>();
                for (int i = 0; i < idMonopattini.length(); i++)
                {
                    String index = String.valueOf(i);
                    Scooter s = new Scooter( idMonopattini.getInt(index), latitutdineMonopattini.getString(index),
                            longitudineMonopattini.getString(index), batteriaMonopattini.getString(index) );
                    nearScooters.add(s);
                }
                result = true;
                Scooter.getNearScooters(nearScooters);
            } catch (JSONException e) {
                Log.d(TAG,"JSON Exception sollevata");
                e.printStackTrace();
            }
        }
        else
        {
            Log.d(TAG,"Nessun monopattino trovato");
        }
        return result;
    }
}