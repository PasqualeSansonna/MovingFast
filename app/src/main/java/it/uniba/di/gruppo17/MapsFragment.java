package it.uniba.di.gruppo17;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.net.MalformedURLException;
import java.net.URL;

import it.uniba.di.gruppo17.asynhttp.AsyncGetScooters;
import it.uniba.di.gruppo17.util.Scooter;

import static it.uniba.di.gruppo17.util.Keys.MAP_ANIMATION_DURATION;
import static it.uniba.di.gruppo17.util.Keys.ZOOM;
import static it.uniba.di.gruppo17.util.Keys.SERVER;
import static it.uniba.di.gruppo17.util.Keys.RAGGIO;

/**
 * @author Francesco Moramarco
 */
public class MapsFragment extends Fragment {

    private static final String TAG ="MapsFragment";
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            mGoogleMap = googleMap;
            mGoogleMap.setMyLocationEnabled(true);
            getDeviceLocation();
            googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                   setScootersMarker();
                }
            });

        }
    };

    /**
     * Metodo per ottenere la poszione corrente del dispositivo
     * @author Francesco Moramarco
     */
    private void getDeviceLocation()
    {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getContext());
        try{
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if ( task.isSuccessful() )
                    {
                        Location currentLocation = (Location) task.getResult();
                        cameraAnimation(currentLocation);
                        Snackbar.make(getView(),R.string.device_position_retrieved_message, Snackbar.LENGTH_LONG).show();
                    }
                    else
                        Snackbar.make(getView(),R.string.device_position_NOT_retrieved_message,Snackbar.LENGTH_SHORT).show();
                }
            });
        }catch (SecurityException e )
        {
            Log.d(TAG,"Security Exception");
        }
    }

    /**
     * Metodo per modificare l'animazione della telecamera della mappa
     * @param currentLocation
     */
    private void cameraAnimation(final Location currentLocation)
    {
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, ZOOM);
        mGoogleMap.animateCamera(cameraUpdate, MAP_ANIMATION_DURATION, new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Snackbar.make(getView(),R.string.loading_scooter_message, Snackbar.LENGTH_SHORT).show();
                getScooters(currentLocation);
            }

            @Override
            public void onCancel() {

            }
        });
    }

    /**
     * @author Francesco Moramarco
     * Metodo per ottenere l'elenco dei monopottino dispoonibili dal server
     */
    private void getScooters(Location currentLocation)
    {
        String serverAddress = SERVER + "getMonopattini.php?r="+RAGGIO+"&lat="+currentLocation.getLatitude()+"&long="+currentLocation.getLongitude();
        AsyncGetScooters getScooters = null;
        try{
            URL urlScooters = new URL (serverAddress);
            getScooters = new AsyncGetScooters();
            getScooters.execute(urlScooters);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Francesco Moramarco
     * Metodo per visualizzare i monopattini sulla mappa mediante marker
     */
    private void setScootersMarker()
    {
         for(Scooter m : Scooter.nearScooters )
        {
            LatLng coordinate = new LatLng( Double.parseDouble( m.getLatitude() ), Double.parseDouble( m.getLongitude() ) );
            MarkerOptions mMarkerOptions = new MarkerOptions()
                    .position( coordinate )
                    .title(getResources().getString(R.string.title_marker_map)+" #"+m.getIdScooter())
                    .snippet(getResources().getString(R.string.snippet_marker_map)+": "+m.getBatteryLevel()+"%")
                    .visible(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE) );
            mGoogleMap.addMarker( mMarkerOptions );
        }
         Snackbar.make(getView(),R.string.completed,Snackbar.LENGTH_SHORT).show();
    }

}
