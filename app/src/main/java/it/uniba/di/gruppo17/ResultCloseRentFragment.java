package it.uniba.di.gruppo17;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import it.uniba.di.gruppo17.asynchttp.JsonFromHttp;
import it.uniba.di.gruppo17.util.Keys;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getActivity().getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        container.removeAllViews();
        return inflater.inflate(R.layout.fragment_result_close_rent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        final String timeString = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toHours(time), TimeUnit.MILLISECONDS.toMinutes(time));
        if ( time > -1 )
            timeTextView.setText(timeString);
        else
            timeTextView.setText("Errore");

        float distance = prefs.getFloat(Keys.TRAVELED_DISTANCE, -1 );
        if ( distance > -1 )
            distanceTextView.setText( String.format("%.2f",distance) );
        else
            distanceTextView.setText("Errore");

        final float amount = Keys.UNLOCK_COST + ( Keys.COST_PER_MINUTE * TimeUnit.MILLISECONDS.toMinutes(time) )
                                        + ( Keys.COST_PER_MINUTE * TimeUnit.MILLISECONDS.toHours(time) );
        if ( amount >= 0 )
            costTextView.setText( String.format("%.2f",amount) );
        else
            costTextView.setText("Errore");

        final int userId = prefs.getInt(Keys.USER_ID,-1);
        final int scooterId = prefs.getInt(Keys.SCOOTER_ID, -1);
        final int rentId = prefs.getInt(Keys.RENT_ID, -1);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String server = Keys.SERVER + "payment.php?idU="+userId+"&idM="+scooterId+"&idN="+rentId+"&amount="+amount+"&time="+timeString;
                try {
                    URL url = new URL(server);
                    AsyncCheckWallet mAsyncCheckWallet = new AsyncCheckWallet();
                    mAsyncCheckWallet.execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkPayment(Boolean paymentCompleted)
    {
        if ( paymentCompleted )
        {
            completed = true;
            removeData();
            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setMessage("Pagmento completato con successo")
                    .setPositiveButton("Termina", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Fragment toHome = new HomeFragment();
                            getFragmentManager().beginTransaction().detach(ResultCloseRentFragment.this).attach(toHome).commit();
                        }
                    })
            .create()
            .show();
        }
        else
        {
            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setMessage("Qualcosa è andato storto").
                    setPositiveButton("Riprova", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getFragmentManager().beginTransaction().detach(ResultCloseRentFragment.this).attach(ResultCloseRentFragment.this).commit();
                        }
                    })
            .create()
            .show();
        }
    }

    private void removeData()
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(Keys.CHRONOMETER_TIME,-1);
        editor.putInt(Keys.RENT_ID,-1);
        editor.putFloat(Keys.TRAVELED_DISTANCE,-1);
        editor.putInt(Keys.SCOOTER_ID,-1);
        editor.putBoolean(Keys.IN_RENT,false);
    }
    private void checkFailedPayment(Boolean lowBalance)
    {
        if ( lowBalance )
            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
                    .setTitle("Credito insufficiente")
                    .setMessage("Non è possibile effettuare il pagamento. Il tuo credito non è sufficiente. Devi ricaricare: "+denaroMancante)
                    .setPositiveButton("Ricarica", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Fragment toPayFragment = new PayFragment();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.hide(ResultCloseRentFragment.this);
                            fragmentTransaction.attach(toPayFragment);
                        }
                    });
    }

    private void afterTask(Boolean[] booleans)
    {
        if ( booleans.length == 1)
        {
            checkPayment(booleans[0]);
        }
        else
        {
            checkFailedPayment(booleans[1]);
        }
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
            Boolean[] result = {false,false};
            if ( isConnectionAvailable() )
            {
                if ( checkServer() )
                {
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
