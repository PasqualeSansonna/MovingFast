package it.uniba.di.gruppo17;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
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

import java.net.URL;
import java.util.concurrent.TimeUnit;

import it.uniba.di.gruppo17.util.Keys;

public class ResultCloseRentFragment extends Fragment {

    public static SharedPreferences prefs;
    private AlertDialog mAlertDialog;
    private TextView timeTextView;
    private TextView distanceTextView;
    private TextView costTextView;
    private Button paymentButton;
    private Button reportAproblemButton;

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
        String timeString = String.format("%02d:%02d",
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

        float amount = Keys.UNLOCK_COST + ( Keys.COST_PER_MINUTE * TimeUnit.MILLISECONDS.toMinutes(time) )
                                        + ( Keys.COST_PER_MINUTE * TimeUnit.MILLISECONDS.toHours(time) );


        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(Keys.CHRONOMETER_TIME,-1);
        editor.putInt(Keys.ID_RENT,-1);
        editor.putFloat(Keys.TRAVELED_DISTANCE,-1);
        editor.putInt(Keys.ID_SCOOTER,-1);
        editor.putBoolean(Keys.IN_RENT,false);
    }

    private class AsyncCheckWallet extends AsyncTask<URL, Void, Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAlertDialog.show();
        }


        @Override
        protected Boolean doInBackground(URL... urls) {
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mAlertDialog.dismiss();
        }
    }

}
