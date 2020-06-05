package it.uniba.di.gruppo17.asynchttp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import it.uniba.di.gruppo17.util.Rental;

public class AsyncPastRentals extends AsyncTask<URL, Void, ArrayList<Rental>> {

    protected ArrayList<Rental> doInBackground(URL... params) {

        ArrayList<Rental> rentals = null;
        JSONObject rentals_json = JsonFromHttp.getJsonObject(params[0]);


        if( rentals_json !=null && rentals_json.length()!=0 ) {

            try{

                JSONObject data = rentals_json.getJSONObject("data");
                JSONObject ora_inizio = rentals_json.getJSONObject("ora_inizio");
                JSONObject ora_fine = rentals_json.getJSONObject("ora_fine");
                JSONObject durata_totale = rentals_json.getJSONObject("durata_totale");
                JSONObject importo = rentals_json.getJSONObject("importo");
                JSONObject longitudine_arrivo = rentals_json.getJSONObject("longitudine_arrivo");
                JSONObject latitudine_arrivo = rentals_json.getJSONObject("latitudine_arrivo");
                JSONObject longitudine_partenza = rentals_json.getJSONObject("longitudine_partenza");
                JSONObject latitudine_partenza = rentals_json.getJSONObject("latitudine_partenza");
                rentals = new ArrayList<>();

                    for (int i = 0; i < data.length(); i++) {
                        String index = String.valueOf(i);
                        Rental rental = new Rental(data.getString(index), ora_inizio.getString(index).substring(0,5), ora_fine.getString(index).substring(0,5), convertTime(durata_totale.getString(index).substring(0,5)), Float.parseFloat(importo.getString(index)), longitudine_arrivo.getString(index), latitudine_arrivo.getString(index), longitudine_partenza.getString(index), latitudine_partenza.getString(index));
                        Log.d("CHECKJSON", rental.toString());
                        rentals.add(rental);
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rentals;
    }

    public Integer convertTime(String time_json){

        String ora = time_json.substring(0,2);
        String minuti = time_json.substring(3,5);

        Integer ora_int = Integer.parseInt(ora);
        Integer minuti_int = Integer.parseInt(minuti);

        minuti_int += (ora_int * 60);

        return minuti_int;
    }

}
