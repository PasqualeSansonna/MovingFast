package it.uniba.di.gruppo17.scooter;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * @author Francesco Moramarco
 */

public class LocationService extends Service {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final static long INTERVALLO_AGGIORNAMENTO = 4 * 1000;
    private final static long INTERVALLO_BREVE = 2000;
    private static Location currentLocation;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (Build.VERSION.SDK_INT >= 26)
        {
            String CHANNEL_ID ="Scooter_notification";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"ScooterChannel", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("")
                    .build();
            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocation();
        return START_NOT_STICKY;
    }

    private void getLocation()
    {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVALLO_AGGIORNAMENTO);
        mLocationRequest.setFastestInterval(INTERVALLO_BREVE);
        LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) )
        {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult (LocationResult locationResult)
                {
                    Location location = locationResult.getLastLocation();
                    if(location!=null)
                    {
                        currentLocation = new Location( location );
                    }
                }
            }, Looper.myLooper());
        }
        else
        {
            stopSelf();
            return;
        }
    }

    public static Location realTimeDeviceLocation()
    {
        return currentLocation;
    }
}