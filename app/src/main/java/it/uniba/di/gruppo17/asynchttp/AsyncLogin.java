package it.uniba.di.gruppo17.asynchttp;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.net.URL;

import it.uniba.di.gruppo17.util.UserProfile;

public class AsyncLogin extends AsyncTask<URL, String, UserProfile> {

    @Override
    protected UserProfile doInBackground(URL... params) {

        UserProfile user = null;

        for (URL param : params) {
            JSONObject json = new JsonFromHttp().getJsonObject(param);
            try {
                if (json.getInt("manutentore") == 0)
                {
                    user = new UserProfile(json.getInt("id"), json.getString("nome"),
                            json.getString("cognome"), json.getString("email"), false);
                }
                else
                {
                    user = new UserProfile(json.getInt("id"), json.getString("nome"),
                            json.getString("cognome"), json.getString("email"), true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                user = new UserProfile(-1, "", "", "", false);
            }
        }
        return user;
    }
}
