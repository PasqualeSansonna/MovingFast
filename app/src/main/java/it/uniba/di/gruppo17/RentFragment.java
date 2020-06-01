package it.uniba.di.gruppo17;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniba.di.gruppo17.util.ConnectionUtil;
import it.uniba.di.gruppo17.util.Keys;


/**
 * Classe che implemente il fragment per la creazione del noleggio
 * Include l'uso dell'NFC con messaggi NDEF e Android Beam
 * @author Andrea Montemurro
 */
public class RentFragment extends Fragment implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    public static SharedPreferences prefs;
    private int userId;
    private NfcAdapter nfcAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getActivity().getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       return inflater.inflate(R.layout.fragment_rent, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Creazione NFC Adptr
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        //Controllo se il dispositivo è dotato di chip nfc
        if ( deviceHasNfc() )
        {
            //Controllo se nfc e android beam sono attivi
            if ( deviceNfcIsOn() && androidBeamIsOn() )
            {
                //Controllo la connessione a Internet
                if ( checkInternetConnection() )
                {
                    //metodo per il richiamo del sender messaggio NDEF
                    nfcAdapter.setNdefPushMessageCallback(this, getActivity());
                    //metodo per il richiamo del metodo da eseguire a trasmissione completata
                    nfcAdapter.setOnNdefPushCompleteCallback(this, getActivity());
                }
            }
        }
    }

    /**
     * Metodo di test per la connessione dati. Se non vi è connessione, il fragment viene riavviato finchè essa non viene stabilita
     */
    private boolean checkInternetConnection() {
        boolean connected = ConnectionUtil.checkInternetConn(this.getActivity());
        if (!connected)
            showDialog(this.getActivity(), getString(R.string.no_connection_title), getString(R.string.connection_disabled));
        return connected;
    }


    /**
     * Crea il dialog e cambia Fragment (riavvia)
     * @param activiy
     * @param title
     * @param message
     */
    private void showDialog(final Activity activiy, String title, CharSequence message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activiy);
        if (title != null) builder.setTitle(title);
        builder.setMessage(message).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeFragment(new RentFragment());
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Metodo che controlla se il dispotivo è dotato di chip NFC
     * @return true se chip nfc è presente, false altrimenti
     */
    private boolean deviceHasNfc() {
        if (nfcAdapter == null) {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle(R.string.noNfc_title)
                    .setCancelable(false)
                    .setMessage(R.string.noNfc_message)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getFragmentManager().popBackStack();
                        }
                    })
                    .create()
                    .show();
            return false;
        }
        return true;
    }

    /**
     * Metodo che controlla se NFC del dispositivo è attivo
     * @return true se nfc è attivo, false altrimenti
     */
    private boolean deviceNfcIsOn()
    {
        if ( nfcAdapter.isEnabled() )
            return true;
        else
        {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle(R.string.nfcIsOff_title)
                    .setMessage(R.string.nfcIsOff_message)
                    .setPositiveButton(R.string.settingsButton, new DialogInterface.OnClickListener() {
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

    /**
     * Metodo che controlla se la funzione android beam è attiva
     * @return true se android beam è attivo, false altrimenti
     *
     */
    private boolean androidBeamIsOn()
    {
        if ( nfcAdapter.isNdefPushEnabled() )
            return true;
        else
        {
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle(R.string.androidBeamIsOff_title)
                    .setMessage(R.string.androidBeamIsOff_message)
                    .setPositiveButton(R.string.settingsButton, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
                        }
                    })
                    .create()
                    .show();
            return false;
        }
    }

    /**
     * Metodo che crea il messaggio da mandare tramite nfc
     *
     * @param event
     * @return messaggio da mandare con proto nfc
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        userId = prefs.getInt(Keys.USER_ID, -1);
        String request;
        request = Keys.RENT + ":" + userId;
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", request.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        changeFragment(new ResultFragment());
    }


    /**
     * Metodo richiamato da OnNdefPuchComplete per cambiare fragment per il fragment risultato
     * @param fragment Fragment risultato
     */
    private void changeFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = this.getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container, fragment,"ResultFragment");
        fragmentTransaction.commit();
    }
}
