package it.uniba.di.gruppo17;

import androidx.fragment.app.FragmentActivity;

import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

import it.uniba.di.gruppo17.util.Rental;

import static it.uniba.di.gruppo17.util.Keys.MAP_ANIMATION_DURATION;
import static it.uniba.di.gruppo17.util.Keys.ZOOM;

/** Activity che verrà visualizzata al momento del click sull'ImageButton presente
 * sulla cardView dello storico noleggi che mostrerà posizione partenza e arrivo
 **/
public class MapsActivityPastRentals extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_past_rentals);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Rental rental;

        /** Prendo il noleggio di riferimento dalla cardView **/
        Bundle dati_passati = getIntent().getExtras();
        rental = (Rental) dati_passati.getParcelable("rental");
        Geocoder mGeocoder = new Geocoder(getApplicationContext(), Locale.ITALY);

        /** Setto le coordinate ed aggiungo i marker**/
        LatLng partenza = new LatLng(Double.parseDouble(rental.getLatitudine_partenza()), Double.parseDouble(rental.getLongitudine_partenza()));
        try {
            mMap.addMarker(new MarkerOptions().position(partenza).title(getString(R.string.departure) + mGeocoder.getFromLocation(partenza.latitude, partenza.longitude, 1).get(0).getAddressLine(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Geocoder mGeocoder2 = new Geocoder(getApplicationContext(), Locale.ITALY);
        LatLng arrivo = new LatLng(Double.parseDouble(rental.getLatitudine_arrivo()), Double.parseDouble(rental.getLongitudine_arrivo()));
        try {
            mMap.addMarker(new MarkerOptions().position(arrivo).title(getString(R.string.arrival) + mGeocoder2.getFromLocation(partenza.latitude, partenza.longitude, 1).get(0).getAddressLine(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLng(partenza));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(arrivo));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(partenza, ZOOM);
        mMap.animateCamera(cameraUpdate, MAP_ANIMATION_DURATION, new GoogleMap.CancelableCallback() {

            @Override
            public void onFinish() {
                Toast.makeText(MapsActivityPastRentals.this,  R.string.travel, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }
        });
    }
}
