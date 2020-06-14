package it.uniba.di.gruppo17;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import it.uniba.di.gruppo17.asynchttp.AsyncGetBalance;
import it.uniba.di.gruppo17.asynchttp.AsyncPastRentals;
import it.uniba.di.gruppo17.util.ConnectionUtil;
import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.Rental;

/**
 * Fragment che mostra l'ammontare del portafoglio dell'utente e gli permette di fare una ricarica
 * @author Andrea Montemurro
 */

public class WalletFragment extends Fragment {

    private static SharedPreferences preferences;
    private Toolbar toolbar;
    private Snackbar snackbar;
    private URL url;
    private int id; //id utente da Sh prefs

    /*Variabile che indica il saldo e relativa TextView in cui mostrarlo*/
    private float amount;
    private static TextView amountTv;

    /*Bottone per ricaricare portafoglio*/
    private Button payBt;

    /* per visualizzare lo sconto*/
    private Button displayDiscountBt;
    private TextView discount_TV;
    private TextView discount_number_TV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getContext().getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        toolbar = ((MainActivity) getActivity()).findViewById(R.id.toolbar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout =  inflater.inflate(R.layout.fragment_wallet, container, false);
        amountTv = layout.findViewById(R.id.walletAmount);
        payBt = layout.findViewById(R.id.ricarica);
        displayDiscountBt = layout.findViewById(R.id.displayDiscountButton);
        discount_TV = layout.findViewById(R.id.discountTextView);
        discount_number_TV = layout.findViewById(R.id.discountNumber);
        discount_number_TV.setVisibility(View.GONE);

        return layout;
    }


    @Override
    public void onResume() {
        // Mostro snackbar
        if (ConnectionUtil.checkInternetConn(Objects.requireNonNull(getActivity()))) {
            Snackbar.make(Objects.requireNonNull(getView()), R.string.loading_connection_msgWallet, Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(Objects.requireNonNull(getView()),R.string.no_connection_title, Snackbar.LENGTH_LONG).show();
        }

        //prendo dati da shared prefs
        amount = preferences.getFloat(Keys.WALLET, 0.00f);
        id = preferences.getInt(Keys.USER_ID, -1);

        //Costruzione stringa HTTP
        String str = Keys.SERVER + "get_portafoglio.php?id=" + id;
        try {
            url = new URL(str);
            AsyncGetBalance http = new AsyncGetBalance();
            http.execute(url);
        } catch (MalformedURLException e) {
            //stampo errore e torno a home Fragment
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.no_connection_title)
                    .setMessage(R.string.no_connection_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    }).create().show();
        }

        /* getView().setFocusableInTouchMode(true);
        getView().requestFocus();
       getView().setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
               // Intent intent = new Intent(getContext(), MainActivity.class);
              //  startActivity(intent);
              //  getActivity().finish();
               getFragmentManager().popBackStack();
                return true;
            }

        } );*/

        payBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFragment(new PayFragment());
            }
        });

        displayDiscountBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer discount = -1;

                displayDiscountBt.setVisibility(View.GONE);

                id = preferences.getInt(Keys.USER_ID, -1);


                //calcolo la durata totale
                int durata_totale = 0;
                Integer ora_totale = 0;
                ArrayList<Rental> rentals;

                //Creo url connessione
                String str = Keys.SERVER + "view_past_rentals.php?id=" + id;
                URL url;

                try {
                    url = new URL(str);
                    AsyncPastRentals pastRentals = new AsyncPastRentals();
                    rentals = pastRentals.execute(url).get(); //lista dei noleggi

                    Log.d("prova", rentals.toString());

                    //Calcolo durata totale noleggi
                    for (int i =0; i<rentals.size(); i++) {

                        durata_totale += rentals.get(i).getDurata();

                    }
                    ora_totale =  durata_totale / 60;
                    Log.d("prova ore", ora_totale.toString());
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                switch (ora_totale){
                    case  1:
                        discount = 10;
                        break;
                    case 3:
                        discount = 20;
                        break;
                    case 10:
                        discount = 25;
                        break;
                    default:
                        discount = 0;
                    }


                Log.d("prova sconto", discount.toString());

                if (discount == 0) {
                    discount_TV.setText(R.string.no_discount);
                    discount_number_TV.setVisibility(View.GONE);
                }
                else {
                    discount_number_TV.setVisibility(View.VISIBLE);
                    discount_TV.setText(R.string.discount_text);
                    discount_number_TV.setText(discount + "%");
                }

                }



        });

        super.onResume();
    }

    private void goToFragment(Fragment nextFragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.commit();
    }


    public static void afterTask(Double aDouble) {
        String soldiStringa;
        if((aDouble < 0.00) || aDouble == null)
        {
            soldiStringa = String.format("%,.2f", 0.00);
        }
        else
        {
            soldiStringa = String.format("%,.2f", aDouble);
        }
        amountTv.setText("€ " + soldiStringa);
    }


}
