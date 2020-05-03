package it.uniba.di.gruppo17;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import it.uniba.di.gruppo17.asynchttp.JsonFromHttp;
import it.uniba.di.gruppo17.services.LocationService;
import it.uniba.di.gruppo17.util.Keys;

/**
 * @author Andrea Montemurro
 * Fragment che viene inserito nello stackback dopo il fragment del noleggio per verificare il risultato
 */


public class ResultFragment extends Fragment {
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor editor;
    private Button button;
    private ImageView imageView;
    private TextView textView;
    private String next;
    private int idScooter;
    private int rentResult;
    private ProgressDialog progressDialog;
    private AsyncGet httpGetNoleggio;
    private Drawable drawable;
    private Toolbar toolbar;
    private boolean executed = false;
    private boolean success = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        prefs = this.getActivity().getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        toolbar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
        httpGetNoleggio = new AsyncGet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_result, container, false);

        final View layout = inflater.inflate(R.layout.fragment_result, container, false);

        button = (Button) layout.findViewById(R.id.buttonGoAhead);
        imageView = (ImageView) layout.findViewById(R.id.imageViewResult);
        textView = (TextView) layout.findViewById(R.id.textView);
        progressDialog = new ProgressDialog(this.getActivity());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nextFragment;
                if (next.equals(Keys.MAP_FRAGMENT)) {
                    nextFragment = new MapsFragment();
                    toolbar.setTitle(R.string.map_title);
                } else {
                    nextFragment = new RentFragment();
                    toolbar.setTitle(R.string.rent_title);
                }
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.commit();
            }
        });
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (executed) {
            if (success)
                goAhead();
            else
                goBack();
        } else {
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle(getString(R.string.loading_connection_title));
            progressDialog.setMessage(getString(R.string.loading_connection_msg));
            progressDialog.show();
            progressDialog.setCancelable(false);

            int id = prefs.getInt(Keys.ID_UTENTE, -1);
            String str = Keys.SERVER + "get_idnoleggio.php?id=" + id;
            URL url = null;

            try {
                url = new URL(str);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            httpGetNoleggio.execute(url);
        }
    }

    private void goAhead() {
        drawable = getResources().getDrawable(R.drawable.android_smile);
        imageView.setImageDrawable(drawable);
        next = Keys.MAP_FRAGMENT;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Keys.IN_RENT, true);
        editor.putInt(Keys.ID_SCOOTER, idScooter);
        editor.putInt(Keys.ID_RENT, rentResult);
        editor.apply();

        textView.setText(getString(R.string.rent_ok));
        textView.setVisibility(View.VISIBLE);

        button.setText(getString(R.string.go_to_map));
        button.setVisibility(Button.VISIBLE);

        imageView.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), LocationService.class);
        intent.putExtra(Keys.SERVICE, Keys.START_UPDATE_POSITION);
        getActivity().startService(intent);
        progressDialog.cancel();
        setNavigationBar();
    }

    /**
     * Metodo che setta visibilità degli item del menu, poichè siamo in noleggio avvenuto bisogna oscurare l'item noleggio
     */
    private void setNavigationBar() {
        NavigationView mNavigationMenu = (NavigationView) getActivity().findViewById(R.id.nav_view);
        Menu menu = mNavigationMenu.getMenu();
        MenuItem menuItem1 = menu.getItem(1);
        menuItem1.setVisible(true);
        MenuItem menuItem2 = menu.getItem(2);
        menuItem2.setVisible(true);
        MenuItem menuItem3 = menu.getItem(3);
        menuItem3.setVisible(false);
        MenuItem menuItem4 = menu.getItem(4);
        menuItem4.setVisible(true);
    }

    private void goBack() {
        imageView.clearAnimation();
        drawable = getResources().getDrawable(R.drawable.android_sad);
        imageView.setImageDrawable(drawable);

        textView.setText(getString(R.string.rent_error));
        textView.setVisibility(View.VISIBLE);

        next = Keys.RENT_FRAGMENT;

        button.setText(getString(R.string.retry_rent));
        button.setVisibility(Button.VISIBLE);

        imageView.setVisibility(View.VISIBLE);
    }



    /**
     * Metodo richiamato nell'onPostExecute dell'asyncTask che, in base al risultato dell'asyncTask, procede con il noleggio oppure torna indietro
    */
    private void afterTask(Integer[] noleggio) {

        int idRent = -1;
        idRent = noleggio[0];
        idScooter = noleggio[1];
        rentResult = idRent;
        executed = true;
        progressDialog.cancel();
        if (idRent != -1) {
            success = true;
            goAhead();
        } else
            goBack();
    }


    /**
     * Classe AsyncTask utilizzata per il prelievo dell'id Noleggio in seguito al noleggio del monopattino
     */
    private class AsyncGet extends AsyncTask<URL, Integer, Integer[]> {


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
            afterTask(integers);
        }
    }
}
