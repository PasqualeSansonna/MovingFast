package it.uniba.di.gruppo17.scooter;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Francesco Moramarco
    Classe che si occupa di effettuare la connessione al server
    tramite HTTP/FTP restituendo il JSON Object risultato della query
    al database posto sul server
 */

public class JsonFromHttp {

    private static final String TAG = "JsonFromHttp";

    public static JSONObject getJsonObject(URL param) {
        JSONObject object = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) param.openConnection();
            connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            int status = connection.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    object = new JSONObject(sb.toString());
                    Log.d(TAG, object.toString());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
