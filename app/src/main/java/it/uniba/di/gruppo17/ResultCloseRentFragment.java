package it.uniba.di.gruppo17;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import it.uniba.di.gruppo17.asynchttp.AsyncPastRentals;
import it.uniba.di.gruppo17.asynchttp.JsonFromHttp;
import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.Rental;

/**
 * @author Francesco Moramarco
 */
public class ResultCloseRentFragment extends Fragment {

    public static SharedPreferences prefs;
    private AlertDialog mAlertDialog;
    private TextView timeTextView;
    private TextView distanceTextView;
    private TextView costTextView;
    private Button paymentButton;
    private Button reportAproblemButton;
    private float denaroMancante = -1;
    private boolean completed = false;

    private int userId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getActivity().getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result_close_rent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundColor(Color.WHITE);
        timeTextView = getView().findViewById(R.id.timeValue);
        distanceTextView = getView().findViewById(R.id.distanceValue);
        costTextView = getView().findViewById(R.id.costValue);
        paymentButton = getView().findViewById(R.id.paymentButton);
        reportAproblemButton = getView().findViewById(R.id.reportButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater mInflater = getActivity().getLayoutInflater();
        builder.setView(mInflater.inflate(R.layout.loading_dialog_layout,null));
        builder.setCancelable(false);
        mAlertDialog = builder.create();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        long time = prefs.getLong(Keys.CHRONOMETER_TIME, -1);
        final String timeString = String.format("%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time), TimeUnit.MILLISECONDS.toSeconds(time), TimeUnit.MILLISECONDS.toSeconds(time));
        int discount = 0;

        if ( time > -1 )
            timeTextView.setText(timeString);
        else
            timeTextView.setText("Errore");

        float distance = prefs.getFloat(Keys.TRAVELED_DISTANCE, -1 );
        if ( distance > -1 )
            distanceTextView.setText( String.format("%.2f",distance)+" km" );
        else
            distanceTextView.setText("Errore");

        userId = prefs.getInt(Keys.USER_ID,-1);
        final int scooterId = prefs.getInt(Keys.SCOOTER_ID, -1);
        final int rentId = prefs.getInt(Keys.RENT_ID, -1);

        float amount = Keys.UNLOCK_COST + ( Keys.COST_PER_MINUTE * TimeUnit.MILLISECONDS.toMinutes(time) )
                + ( Keys.COST_PER_MINUTE * TimeUnit.MILLISECONDS.toHours(time) );

        /** calcolo lo sconto in base al numero delle ore di noleggio**/
        discount = calculateDiscount();

        /** calcolo il prezzo scontato**/
        if (discount != 0){
            float sottocento;
            sottocento = 1 - (discount/ 100);
            amount *= sottocento;
        }

        if ( amount >= 0 )
            costTextView.setText( String.format("%.2f",amount)+" €");
        else
            costTextView.setText("Errore");

        final float finalAmount = amount;
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String server = Keys.SERVER + "payment.php?idU="+userId+"&idM="+scooterId+"&idN="+rentId+"&amount="+ finalAmount +"&time="+timeString;
                try {
                    URL url = new URL(server);
                    AsyncCheckWallet mAsyncCheckWallet = new AsyncCheckWallet();
                    mAsyncCheckWallet.execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        reportAproblemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment nextFragment = new ReportProblemsRentEndedFragment();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.commit();
            }
        });
    }

    private int calculateDiscount(){
        /**calcolo la durata totale**/
        int durata_totale = 0;
        int ora_totale = 0;
        ArrayList<Rental> rentals;
        int sconto = 0;

        /**
         * Creo url connessione
         **/
        String str = Keys.SERVER + "view_past_rentals.php?id=" + userId;
        URL url = null;

        try {
            url = new URL(str);
            AsyncPastRentals pastRentals = new AsyncPastRentals();
            rentals = pastRentals.execute(url).get(); //lista dei noleggi


            /**Calcolo durata totale noleggi**/
            for (int i =0; i< rentals.size(); i++) {

                durata_totale += rentals.get(i).getDurata();
            }

            ora_totale =  durata_totale / 60;

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        switch (ora_totale){
            case  1:
                sconto = 10;
                break;
            case 3:
                sconto = 20;
                break;
            case 10:
                sconto = 25;
                break;
            default:
                sconto = 0;
        }
        return sconto;
    }

    private void paymentSuccess()
    {
        completed = true;
        removeData();
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle(R.string.paymentCompleted_title)
                .setMessage(R.string.paymentCompleted_message)
                .setPositiveButton(R.string.closeButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                })
        .create()
        .show();
    }

    private void removeData()
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Keys.TRAVELED_DISTANCE);
        editor.remove(Keys.CHRONOMETER_TIME);
        editor.putInt(Keys.RENT_ID,-1);
        editor.putInt(Keys.SCOOTER_ID,-1);
        editor.putBoolean(Keys.IN_RENT,false);
        editor.apply();
    }

    private void needToCharge()
    {
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle(R.string.lowBalance_title)
                .setMessage(getString(R.string.lowBalance_message)+" "+denaroMancante+"€")
                .setPositiveButton(R.string.chargeWallet_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Fragment toPayFragment = new PayFragment();
                        FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
                        mFragmentTransaction.addToBackStack(null);
                        mFragmentTransaction.replace(R.id.fragment_container,toPayFragment).commit();
                    }
                })
        .create()
        .show();
    }

    private void afterTask(Boolean[] booleans)
    {
        boolean paymentCompleted = booleans[0];
        boolean lowBalance = booleans[1];
        if ( paymentCompleted && !lowBalance )
        {
            paymentSuccess();
            return;
        }
        if ( !paymentCompleted && lowBalance )
        {
            needToCharge();
            return;
        }
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle(R.string.paymentError_title)
                .setMessage(R.string.paymentError_message)
                .setPositiveButton(R.string.reloadButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getFragmentManager().beginTransaction().detach(ResultCloseRentFragment.this).attach(ResultCloseRentFragment.this).commit();
                    }
                })
                .create()
                .show();
    }

    private class AsyncCheckWallet extends AsyncTask<URL, Void, Boolean[]>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAlertDialog.show();
        }

        @Override
        protected Boolean[] doInBackground(URL... urls)
        {
            Boolean[] result = {false, false};
            if ( isConnectionAvailable() )
            {
                if ( checkServer() )
                {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (URL url : urls )
                    {
                        JSONObject json = new JsonFromHttp().getJsonObject(url);
                        try {
                            result[0] = json.getBoolean("pagamentoCompletato");
                            if ( json.has("creditoInsufficiente"))
                                result[1] = json.getBoolean("creditoInsufficiente");
                            if ( json.has("denaroMancante") )
                                denaroMancante = (float) json.getDouble("denaroMancante");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean[] booleans) {
            super.onPostExecute(booleans);
            mAlertDialog.dismiss();
            afterTask(booleans);
        }

        private boolean isConnectionAvailable()
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if ( activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting() )
                return true;
            else
                return false;
        }

        private boolean checkServer()
        {
            boolean success = false;
            try
            {
                URL url = new URL(Keys.SERVER);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(Keys.TIMEOUT);
                connection.connect();
                success = connection.getResponseCode() == 200;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return success;
        }
    }
}
