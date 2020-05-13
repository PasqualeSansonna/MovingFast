package it.uniba.di.gruppo17.scooter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;
/**
 * @author Francesco Moramarco
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainAcitivity";
    private static final int REQUEST_ACCESS_LOCATION = 2;

    private NfcAdapter mNfcAdapter;
    private ImageView imageError;
    private TextView textViewErrorMessage;
    private CheckInternetConnection mCheckInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageError = findViewById(R.id.imageView_locationError);
        textViewErrorMessage = findViewById(R.id.textView_ErrorMessage);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"OnStart");
        mCheckInternet = new CheckInternetConnection(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG,"OnResume");
        if ( deviceHasNfc() )
        {
            if ( isConnectionAvailable() )
            {
                try {
                    if ( mCheckInternet.execute().get() )
                    {
                        if ( checkLocationPermission() && checkDeviceLocationIsOn() )
                        {
                            startLocationService();
                            if ( deviceNfcIsOn() && androidBeamIsOn() )
                            {
                                Toast.makeText(this,R.string.LocationService_message,Toast.LENGTH_SHORT).show();
                                startActivity(new Intent (this, NfcListener.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                            else
                            {
                                imageError.setVisibility(View.VISIBLE);
                                textViewErrorMessage.setText(R.string.NfcError_message);
                                textViewErrorMessage.setVisibility(View.VISIBLE);
                            }
                        }
                        else
                        {
                            imageError.setVisibility(View.VISIBLE);
                            textViewErrorMessage.setText(R.string.noLocation_message);
                            textViewErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        imageError.setVisibility(View.VISIBLE);
                        textViewErrorMessage.setText(R.string.serverError);
                        textViewErrorMessage.setVisibility(View.VISIBLE);
                    }
                } catch (ExecutionException|InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                imageError.setVisibility(View.VISIBLE);
                textViewErrorMessage.setText(R.string.noConnectionAvailable_message);
                textViewErrorMessage.setVisibility(View.VISIBLE);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.connectionOff_title)
                        .setMessage(R.string.connectionOff_message)
                        .setPositiveButton(R.string.connectionOff_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        })
                        .create()
                        .show();
            }
        }
        else
            setContentView(R.layout.no_nfc);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"OnPause");
        imageError.setVisibility(View.GONE);
        textViewErrorMessage.setVisibility(View.GONE);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        Log.d(TAG,"OnStop");
    }

    private boolean deviceHasNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.noNfc_alertDialog_title)
                    .setMessage(R.string.noNfc_alerDialog_message)
                    .setPositiveButton(R.string.noNfc_alertDialog_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .create()
                    .show();
            return false;
        }
        return true;
    }

    private boolean deviceNfcIsOn()
    {
        if ( mNfcAdapter.isEnabled() )
            return true;
        else
        {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.deviceNFC_isOff_title)
                    .setMessage(R.string.deviceNFC_isOff_message)
                    .setPositiveButton(R.string.deviceNFC_isOff_settingsButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                            else
                                startActivity(new Intent (Settings.ACTION_WIRELESS_SETTINGS));
                        }
                    })
                    .create()
                    .show();
            return false;
        }
    }

    private boolean androidBeamIsOn()
    {
        if ( mNfcAdapter.isNdefPushEnabled() )
            return true;
        else
        {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.androidBeam_isOff_title)
                    .setMessage(R.string.androidBeam_isOff_message)
                    .setPositiveButton(R.string.androidBeam_isOff_settingsButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
                        }
                    })
                    .create()
                    .show();
            return false;
        }
    }

    private boolean isConnectionAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if ( activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting() )
            return true;
        else
            return false;
    }

    private boolean checkLocationPermission()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            //L'app non dispone dei permessi necessari
            if ( ActivityCompat.shouldShowRequestPermissionRationale( this, Manifest.permission.ACCESS_FINE_LOCATION ) )
            {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.locationPermission_title)
                        .setMessage(R.string.locationPermission_message)
                        .setPositiveButton(R.string.locationPermission_positiveButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
                            }
                        })
                        .create()
                        .show();
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION);
            }
            return false;
        }
        else
        {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult( int requestCode, String[]permissions, int[] grantResults )
    {
        if ( requestCode == REQUEST_ACCESS_LOCATION )
        {
            if ( grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                checkDeviceLocationIsOn();
            }
            else
            {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.locationPermission_title)
                        .setMessage(R.string.locationPermission_message)
                        .setPositiveButton(R.string.locationPermission_positiveButton, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_LOCATION );
                            }
                        })
                        .create()
                        .show();
            }
        }
    }

    private boolean checkDeviceLocationIsOn()
    {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.deviceLocationOn_title)
                    .setMessage(R.string.deviceLocationOn_message)
                    .setPositiveButton(R.string.deviceLocationOn_positiveButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .create()
                    .show();
            return false;
        }
        return true;
    }

    //service di posizione in background per avere aggiornamenti continui sulla posizione del dispositivo
    private void startLocationService()
    {
        if(!isLocationServiceRunning())
        {
            Intent locationServiceIntent = new Intent(this, LocationService.class);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                MainActivity.this.startForegroundService(locationServiceIntent);
            }
            else
                startService(locationServiceIntent);
        }
    }

    private boolean isLocationServiceRunning()
    {
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE) )
        {
            if(LocationService.class.getName().equals(service.service.getClassName()) )
                return true;
        }
        return false;
    }
}
