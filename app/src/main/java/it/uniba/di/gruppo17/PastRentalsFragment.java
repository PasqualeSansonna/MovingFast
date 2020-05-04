package it.uniba.di.gruppo17;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.Edits;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import it.uniba.di.gruppo17.asynchttp.AsyncPastRentals;
import it.uniba.di.gruppo17.services.LocationService;
import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.PastRentalsAdapter;
import it.uniba.di.gruppo17.util.Rental;

import static android.content.Context.MODE_PRIVATE;
import static it.uniba.di.gruppo17.util.Keys.ID_UTENTE;
import static it.uniba.di.gruppo17.util.Keys.RENTALS_TOTAL_DURATION;


public class PastRentalsFragment extends Fragment {

    ArrayList<Rental> rentals = null;
    ArrayList<Rental> rentals_serializable = null;
    Rental rental;
    private Integer user_ID;
    private SharedPreferences preferences;
    private TextView TV_total_duration;
    private TextView TV_num_rentals;
    RecyclerView recyclerView;
    PastRentalsAdapter pastRentalsAdapter;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_past_rentals, container, false);
        TV_total_duration = (TextView) view.findViewById(R.id.valueDurationTextView);
        TV_num_rentals = (TextView) view.findViewById(R.id.valueTotalRentalTextView);

        viewPastRentals();

        return view;
    }

    private void viewPastRentals(){

        int durata_totale = 0;
        int ora_totale = 0;
        int minuti_totale = 0;
        Iterator<Rental> iterator;
        /**
         * Creo url connessione
         **/
        preferences = getActivity().getSharedPreferences("MovingFastPreferences", MODE_PRIVATE);
        user_ID = Integer.parseInt(preferences.getAll().get(ID_UTENTE).toString());

        String str = Keys.SERVER + "view_past_rentals.php?id=" + user_ID;
        URL url = null;

        try {
            url = new URL(str);
            AsyncPastRentals pastRentals = new AsyncPastRentals();
            rentals = new ArrayList<>();
            rentals = pastRentals.execute(url).get();

            pastRentalsAdapter = new PastRentalsAdapter(rentals);

            String string = String.valueOf(rentals.size());
            TV_num_rentals.setText(string);

            for (int i =0; i< rentals.size(); i++) {
                durata_totale += rentals.get(i).getDurata();
            }

            ora_totale = durata_totale / 60;
            minuti_totale = durata_totale % 60;

            /**Nelle shared preferences salviamo la durata totale dei noleggi effettuati utile per lo sconto (in minuti)**/
            writePreferences(durata_totale);
            TV_total_duration.setText(ora_totale+ "h " + minuti_totale + "m");

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.pastRentalsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(pastRentalsAdapter);

    }

    private void writePreferences(float durata_totale) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(RENTALS_TOTAL_DURATION, Float.parseFloat(String.format("%.2f", durata_totale)));
            editor.apply();
    }


}
