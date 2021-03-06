package it.uniba.di.gruppo17.asynchttp;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.net.URL;


/**@author Pasquale, Sgarra
 * Async task connessione al db e modifica profilo,
 * ritorno boolean true se operazione avvenuta con successo
 */
public class AsyncEditProfile extends AsyncTask<URL, String, Boolean> {

    @Override
    protected Boolean doInBackground(URL... params) {
        Boolean flag = false;
        for (URL param : params) {
            JSONObject result = new JsonFromHttp().getJsonObject(param);
            try {
                if (result.getString("ok").equals("true")){
                    flag = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
}
