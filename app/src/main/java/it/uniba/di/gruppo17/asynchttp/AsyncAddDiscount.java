package it.uniba.di.gruppo17.asynchttp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class AsyncAddDiscount extends AsyncTask<URL, Integer, Boolean> {

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
