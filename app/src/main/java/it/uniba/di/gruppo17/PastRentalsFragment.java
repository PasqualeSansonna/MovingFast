package it.uniba.di.gruppo17;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.Rental;

import static it.uniba.di.gruppo17.util.Keys.ID_UTENTE;


public class PastRentalsFragment extends Fragment {

    GridLayout grid_past_rentals;
    TextView TV_data, TV_tempo, TV_importo;

    private Rental rental;
    private Integer user_ID;
    SharedPreferences preferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_past_rentals, container, false);



        viewPastRentals();

        return view;
    }

    private void viewPastRentals() {

        /**Acquisisco ID dell'utente autenticato per visualizzazarne nome,cognome ed email
         * e creo url connessione
         **/
        preferences = getActivity().getSharedPreferences("MovingFastPreferences", Context.MODE_PRIVATE);
        user_ID = Integer.parseInt(preferences.getAll().get(ID_UTENTE).toString());
        String str = Keys.SERVER + "view_pastRentals.php?id=" + user_ID;


    }
}
