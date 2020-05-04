package it.uniba.di.gruppo17.asynchttp;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.net.URL;

import it.uniba.di.gruppo17.util.UserProfile;

/**@author Pasquale, Sgarra
 * Async task  visualizzazione profilo,
 * ritorno UserProfile se operazione avvenuta con successo
 */
public class AsyncViewProfile extends AsyncTask<URL,String, UserProfile> {

    @Override
    protected UserProfile doInBackground(URL... params) {

        UserProfile user = null;

        for (URL param : params) {
            JSONObject utente = new JsonFromHttp().getJsonObject(param);
            try {
                    user = new UserProfile(utente.getString("nome"), utente.getString("cognome"), utente.getString("email"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return user;
    }
}
