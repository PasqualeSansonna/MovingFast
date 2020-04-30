package it.uniba.di.gruppo17.asynchttp;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.net.URL;

import it.uniba.di.gruppo17.util.Rental;

public class AsyncPastRentals extends AsyncTask<URL,String, Rental> {

    protected Rental doInBackground(URL... params) {

        Rental rental = null;


        for (URL param : params) {
            JSONObject rental_json = new JsonFromHttp().getJsonObject(param);
            try {
               // rental = new Rental(     );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rental;
    }


}
