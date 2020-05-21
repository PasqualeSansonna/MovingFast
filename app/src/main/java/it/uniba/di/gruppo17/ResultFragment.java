package it.uniba.di.gruppo17;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.uniba.di.gruppo17.asynchttp.AsyncGetRentId;
import it.uniba.di.gruppo17.services.LocationService;
import it.uniba.di.gruppo17.util.Keys;

/**
 * @author Andrea Montemurro, rivisitato completamente da Francesco Moramarco
 * Fragment che viene inserito nello stackback dopo il fragment del noleggio per verificare il risultato
 */


public class ResultFragment extends Fragment implements SensorEventListener, AsyncResponse{

    private static SharedPreferences prefs;
    private TextView distanceValue;
    private Chronometer rentTime;
    private SensorManager sManager;
    private Sensor stepSensor;
    private static long steps = 0;
    private boolean taskExecuted;
    private boolean rentSucceed;
    private AlertDialog mAlertDialog;
    Thread updateDistance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getActivity().getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        sManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_result, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        distanceValue = getView().findViewById(R.id.textViewValueTraveledDistance);
        rentTime = getView().findViewById(R.id.chrono);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater mInflater = getActivity().getLayoutInflater();
        builder.setView(mInflater.inflate(R.layout.loading_dialog_layout,null));
        builder.setCancelable(false);
        mAlertDialog = builder.create();

        LinearLayout closeRentButton = getView().findViewById(R.id.closeRentButton);
        LinearLayout googleMaps = getView().findViewById(R.id.openMapButton);
        LinearLayout report = getView().findViewById(R.id.reportButton);

        closeRentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Fermo il cronometro e il sensore
                rentTime.stop();
                sManager.unregisterListener(ResultFragment.this, stepSensor);
                //rilevo il tempo segnato sul cronometro e la distanza percorosa
                long elapsedTime = SystemClock.elapsedRealtime() - rentTime.getBase();
                float distance = getDistanceRun(steps);
                //Scrivo nelle shared pref. in modo da poterli utilizzare in seguito
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(Keys.CHRONOMETER_TIME,elapsedTime);
                editor.putFloat(Keys.TRAVELED_DISTANCE,distance);
                editor.apply();
                //Passo al fragment per la chiusura del noleggio
                Fragment toCloseRent = new CloseRentFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_result, toCloseRent).commit();
            }
        });

        googleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri googleMapsIntentUri = Uri.parse("geo:"+ LocationService.realTimeDeviceLocation().getLatitude()+","+
                        LocationService.realTimeDeviceLocation().getLongitude());
                Intent googleMapsIntent = new Intent(Intent.ACTION_VIEW, googleMapsIntentUri);
                googleMapsIntent.setPackage("com.google.android.apps.maps");
                startActivity(googleMapsIntent);
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Fragment toCloseRent = new CloseRentFragment();
                //getFragmentManager().beginTransaction().replace(R.id.fragment_result, toCloseRent).commit();
            }
        });

        updateDistance = new Thread()
        {
            @Override
            public void run()
            {
                while(!isInterrupted())
                {
                    try {
                        Thread.sleep(1000);
                        if ( getActivity() != null )
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    distanceValue.setText( String.format("%.2f",getDistanceRun(steps)));

                                }
                            });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        sManager.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_FASTEST);
        if ( taskExecuted )
        {
            if ( rentSucceed )
            {
                //updateDistance.start();
                if ( prefs.contains(Keys.CHRONOMETER_TIME) )
                    setChronometer();
                rentTime.start();
            }
            else
            {
                //Noleggio non è andato a buon fine.
                rentFailed();
            }
        }
        else
        {
            mAlertDialog.show();
            AsyncGetRentId mAsyncGetRentId = new AsyncGetRentId(this);
            int id = prefs.getInt(Keys.USER_ID, -1);
            String strConnection = Keys.SERVER + "get_rent_id.php?idU=" + id;
            URL url = null;
            try {
                url = new URL(strConnection);
                mAsyncGetRentId.execute(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo richiamato subito dopo che le operazioni in background (per ottenere l'id del noleggio) sono terminate
    // Si è resa necessaria questa implementazione per fare in modo che fosse mostrato il dialog di caricamento all'utente
    @Override
    public void getResponse(Integer[] integers) {
        afterTask(integers);
    }

    /**
     * Metodo richiamato  in base al risultato dell'asyncTask, procede con il noleggio oppure mostra una schermata di errore
     */
    private void afterTask(Integer[] values)
    {
        taskExecuted = true;
        int idRent = values[0];
        int idScooter = values[1];
        if ( idRent != -1 && idScooter != -1 )
        {
            //Il noleggio è andato a buon fine
            mAlertDialog.dismiss();
            rentSucceed = true;
            saveData(idScooter, idRent); //salvo nelle shared Pref. i l'id del monopattino e del noleggio
            getActivity().findViewById(R.id.resultLayout).setVisibility(View.VISIBLE);
            //distanceValue.setText( String.valueOf(getDistanceRun(steps)));
            updateDistance.start();
           // rentTime.setBase(SystemClock.elapsedRealtime());
            rentTime.start();
            setNavigationBar();
        }
        else
        {
            mAlertDialog.dismiss();
            rentFailed();
        }
    }

    //Metodo per la memorizzazione dell'id del monopattino e del noleggio
    private void saveData(int idScooter, int idRent) {

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Keys.IN_RENT, true);
        editor.putInt(Keys.SCOOTER_ID, idScooter);
        editor.putInt(Keys.RENT_ID, idRent);
        editor.putLong(Keys.CHRONOMETER_TIME,0);
        editor.putFloat(Keys.TRAVELED_DISTANCE,0);
        editor.apply();
    }

    /**
     * Metodo che setta visibilità degli item del menu, poichè siamo in noleggio avvenuto bisogna oscurare l'item noleggio
     */
    private void setNavigationBar() {
        NavigationView mNavigationMenu = (NavigationView) getActivity().findViewById(R.id.nav_view);
        Menu menu = mNavigationMenu.getMenu();
        MenuItem menuItem0 = menu.getItem(0);
        menuItem0.setVisible(true);
        MenuItem menuItem1 = menu.getItem(1);
        menuItem1.setVisible(true);
        MenuItem menuItem2 = menu.getItem(2);
        menuItem2.setTitle("Noleggio in corso");
        menuItem2.setVisible(true);
        //menuItem2.setVisible(false);
        MenuItem menuItem3 = menu.getItem(3);
        menuItem3.setVisible(true);
        MenuItem menuItem4 = menu.getItem(4);
        menuItem4.setVisible(true);
    }

    private void rentFailed()
    {
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle(R.string.rent_error_title)
                .setMessage(R.string.rent_error)
                .setPositiveButton(R.string.retry_rent, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Fragment toRentFragment = new RentFragment();
                        getFragmentManager().beginTransaction().replace(R.id.fragment_result, toRentFragment).commit();
                        ResultFragment.this.onDestroy();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onPause() {
        super.onPause();
        sManager.unregisterListener(this, stepSensor);
        //Salvo nelle shared pref. data e ora correnti e il tempo registrato dal cronometro
        //In questo modo sarà possibile tenere sempre traccia del tempo trascorso, anche nel caso in cui l'app dovesse essere killata
        SharedPreferences.Editor editor = prefs.edit();
        float traveledDistance = getDistanceRun(steps);
        long elapsedTime = SystemClock.elapsedRealtime() - rentTime.getBase(); //il tempo registrato dal cronometro, in millisecondi
        SimpleDateFormat sdf = new SimpleDateFormat(Keys.PATTERN_DATE_TIME, Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        editor.putLong(Keys.CHRONOMETER_TIME,elapsedTime);
        editor.putString(Keys.CURRENT_DATE_TIME, currentDateAndTime);
        editor.putFloat(Keys.TRAVELED_DISTANCE,traveledDistance);
        editor.apply();
    }

    /**
     * Metodi necessari per il calcolo della distanza percorsa
     */
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mSensor = sensorEvent.sensor;
        float[] values = sensorEvent.values;
        int value = -1;
        if ( values.length > 0 )
            value = (int)values[0];
        if ( mSensor.getType() == Sensor.TYPE_STEP_DETECTOR )
            steps++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private float getDistanceRun(long steps)
    {
        float distance;
        if ( prefs.contains(Keys.TRAVELED_DISTANCE) && prefs.getFloat(Keys.TRAVELED_DISTANCE, -1) != -1 )
            distance = (float) (steps*78)/(float)100000 + prefs.getFloat(Keys.TRAVELED_DISTANCE, 0);
        else
            distance = (float) (steps*78)/(float)100000;
        return distance;
    }

    // Metodo che consente di mostrare sul cronometro il corretto tempo di noleggio anche nel momento in cui l'app non è eseguita in
    // background.
    private void setChronometer()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Keys.PATTERN_DATE_TIME);
        try {
            Date startDateTime = simpleDateFormat.parse( prefs.getString(Keys.CURRENT_DATE_TIME,""));
            SimpleDateFormat sdf = new SimpleDateFormat(Keys.PATTERN_DATE_TIME, Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            Date currentDateTime = simpleDateFormat.parse(currentDateandTime);

            long different = currentDateTime.getTime() - startDateTime.getTime();
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            int elapsedTimeMilliseconds = (int) (elapsedHours * 60 * 60 * 1000
                    + elapsedMinutes * 60 * 1000
                    + elapsedSeconds * 1000);


            rentTime.setBase( SystemClock.elapsedRealtime() - elapsedTimeMilliseconds - prefs.getLong(Keys.CHRONOMETER_TIME,0) );

        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }
}
