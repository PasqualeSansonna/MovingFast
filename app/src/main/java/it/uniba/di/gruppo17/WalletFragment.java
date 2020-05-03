package it.uniba.di.gruppo17;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.net.MalformedURLException;
import java.net.URL;

import it.uniba.di.gruppo17.asynchttp.AsyncGetImporto;
import it.uniba.di.gruppo17.util.ConnectionUtil;
import it.uniba.di.gruppo17.util.Keys;

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

        // Mostro snackbar
        if (ConnectionUtil.checkInternetConn(getActivity()))
        {
            //Snackbar.make(getView(),R.string.loading_connection_msgWallet, Snackbar.LENGTH_LONG).show();
        }
        else
        {
            //Snackbar.make(getView(),R.string.no_connection_title, Snackbar.LENGTH_LONG).show();
        }

        //prendo dati da shared prefs
        amount = preferences.getFloat(Keys.WALLET, 0.00f);
        id = preferences.getInt(Keys.ID_UTENTE, -1);

        //Costruzione stringa HTTP
        String str = Keys.SERVER + "get_portafoglio.php?id=" + id;
        try
        {
            url = new URL(str);
            AsyncGetImporto http = new AsyncGetImporto();
            http.execute(url);
        } catch (MalformedURLException e)
        {
            //stampo errore e torno a home Fragment
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.no_connection_title)
                    .setMessage(R.string.no_connection_message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            goToFragment(new HomeFragment());
                        }
                    }).create().show();
        }

        amountTv = (TextView) layout.findViewById(R.id.wallet_amount);
        payBt =  (Button) layout.findViewById(R.id.ricarica);
        payBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO: 03/05/2020   creare fragment per pagamento
                goToFragment(new PayFragment()); */
            }
        });

        return layout;
    }



    private void goToFragment(HomeFragment nextFragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.commit();
    }


    public static void afterTask(Double aDouble) {
        String soldiStringa = String.format("%,.2f", aDouble);
        amountTv.setText(soldiStringa);
    }


}
