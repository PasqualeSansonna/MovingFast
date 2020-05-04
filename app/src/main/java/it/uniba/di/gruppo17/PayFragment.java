package it.uniba.di.gruppo17;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.ConnectionUtil;
import it.uniba.di.gruppo17.asynchttp.*;

/**
 * Fragment che consente all'utente di aggiungere del credito nel suo portafoglio
 * @author Andrea Montemurro
 */

public class PayFragment extends Fragment {

    private SharedPreferences preferences;
    private Button payBt;
    private EditText amountEt;
    private int idUtente;
    private boolean okTransaction = false; //variabile booleana che verifica se la transazione è andata a buon fine
    private static boolean mOk;
    private Double newAmount; //indica il valore del saldo del portafoglio dopo la transazione
    private URL url;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getActivity().getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_pay, container, false);

        idUtente = preferences.getInt(Keys.ID_UTENTE, -1);
        payBt = (Button) layout.findViewById(R.id.addBalanceButton);
        amountEt = (EditText) layout.findViewById(R.id.valueAddBalance);

        payBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectionUtil.checkInternetConn(getActivity()))
                {
                    Toast.makeText(getActivity(), R.string.loading_connection_msg, Toast.LENGTH_LONG);
                    float amount = Float.parseFloat(amountEt.getText().toString());
                    if (amount > 0.00)
                    {
                        //Se importo ricarica non è nullo posso effettuare la ricarica
                        String strConn = Keys.SERVER + "set_portafoglio.php?id=" + idUtente + "&soldi=" + amount;
                        try {
                            url = new URL(strConn);
                            // Esequo la query tramite php e prendo il risultato
                            okTransaction = new AsyncAddBalance().execute(url).get();
                        } catch (InterruptedException | ExecutionException | MalformedURLException e) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle(R.string.no_connection_title)
                                    .setMessage(R.string.no_connection_message)
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Faccio tornare al fragment Wallet
                                            goToFragment(new WalletFragment());
                                        }
                                    }).create().show();
                        }

                        //verifico che la transazione è andata a buon fine
                        checkTransaction(okTransaction);
                    }
                    else
                    {
                        //errore sull'importo inserito
                        Toast.makeText(getActivity(), R.string.amount_lessThanZero, Toast.LENGTH_LONG);
                    }

                }
                else
                {
                    //errore no connessione
                    Toast.makeText(getActivity(), R.string.no_connection_message, Toast.LENGTH_LONG);
                }
            }
        });

        return layout;
    }


    /**
     * Metodo che fa cambiare fragment
     * @param nextFragment prossimo fragment
     */
    private void goToFragment(Fragment nextFragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, nextFragment);
        fragmentTransaction.commit();
    }



    public static void afterTask(boolean ok){
      mOk=ok;
    }

    /**
     * Metodo che controlla se la transazione è stata fatta o meno
     */
    private void checkTransaction (boolean okTransaction)
    {
        if (okTransaction) {
            Toast.makeText(this.getView().getContext(), R.string.success_transaction, Toast.LENGTH_LONG).show();
            if (setWalletValue())
                goToFragment(new WalletFragment());
        } else {
            Toast.makeText(this.getView().getContext(), R.string.failure_transaction, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Metodo che aggiorna il valore del portafoglio dopo la ricarica
     * @return
     */
    private boolean setWalletValue() {
        SharedPreferences.Editor editor = preferences.edit();
        double amount = -1.0;
        try {
         /*   importo = new AsyncGetBalance().execute(new URL(
                    .concat(String.valueOf(preferences.getInt(Keys.ID_UTENTE, -1))))).get(); */
            String connectinUrl =  Keys.SERVER + "get_portafoglio.php?id=" + preferences.getInt(Keys.ID_UTENTE, -1);
            URL url = new URL(connectinUrl);
            newAmount = new AsyncGetBalance().execute(url).get();
            editor.putFloat(Keys.WALLET, (float) amount);
            editor.apply();
            return true;
        } catch (InterruptedException | ExecutionException | MalformedURLException e) {
            return false;
        }
    }

}
