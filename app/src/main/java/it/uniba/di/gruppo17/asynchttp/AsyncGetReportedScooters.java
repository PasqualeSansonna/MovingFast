package it.uniba.di.gruppo17.asynchttp;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URL;
import java.util.ArrayList;
import it.uniba.di.gruppo17.util.Reporting;
import it.uniba.di.gruppo17.util.Scooter;

/**
 * Classe che permette di recuperare i monopattini scarichi (<50) o che richiedono manutenzione, dal db
 */

public class AsyncGetReportedScooters extends AsyncTask <URL, Void, Boolean>{
    private static final String TAG ="AsyncGetMonopattiniSgn";

    @Override
    protected Boolean doInBackground(URL... params) {

        boolean result = false;
        ArrayList<Scooter> nearScooters = null;
        JSONObject monopattini = JsonFromHttp.getJsonObject(params[0]);
        if ( monopattini !=null && monopattini.length()!=0 )
        {
            try {
                JSONObject idMonopattini = monopattini.getJSONObject("idMonopattino");
                JSONObject latitutdineMonopattini = monopattini.getJSONObject("lat");
                JSONObject longitudineMonopattini = monopattini.getJSONObject("lon");
                JSONObject batteriaMonopattini = monopattini.getJSONObject("batteria");
                JSONObject idSegnalazioni = monopattini.getJSONObject("idSegnalazione");
                JSONObject statoFreniMonopattini = monopattini.getJSONObject("freni");
                JSONObject statoRuoteMonopattini = monopattini.getJSONObject("ruote");
                JSONObject statoManubrioMonopattini = monopattini.getJSONObject("manubrio");
                JSONObject statoAcceleratoreMonopattini = monopattini.getJSONObject("acceleratore");
                JSONObject statoBloccoMonopattini = monopattini.getJSONObject("blocco");
                JSONObject statoAltroMonopattini = monopattini.getJSONObject("altro");
                nearScooters = new ArrayList<>();
                for (int i = 0; i < idMonopattini.length(); i++)
                {
                    String index = String.valueOf(i);
                    Reporting r = new Reporting (idMonopattini.getInt(index), idSegnalazioni.getInt(index), statoFreniMonopattini.getInt(index),
                            statoRuoteMonopattini.getInt(index), statoManubrioMonopattini.getInt(index), statoAcceleratoreMonopattini.getInt(index), statoBloccoMonopattini.getInt(index),
                            statoAltroMonopattini.getInt(index));
                    Scooter s = new Scooter( idMonopattini.getInt(index), latitutdineMonopattini.getString(index),
                            longitudineMonopattini.getString(index), batteriaMonopattini.getString(index), true, r);
                    nearScooters.add(s);
                }
                result = true;
                Scooter.addNearScooters(nearScooters);
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

    private boolean intToBool (int intero)
    {
        if (intero == 0)
            return false;
        else
            return true;
    }
}
