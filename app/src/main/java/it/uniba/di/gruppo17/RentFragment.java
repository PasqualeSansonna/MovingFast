package it.uniba.di.gruppo17;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import it.uniba.di.gruppo17.util.ConnectionUtil;
import it.uniba.di.gruppo17.util.Keys;


/**
 * Classe che implemente il fragment per la creazione del noleggio
 * Include l'uso dell'NFC con messaggi NDEF e Android Beam
 * @author Andrea Montemurro
 */
public class RentFragment extends Fragment implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    public static SharedPreferences prefs;
    private int username;
    private NfcAdapter nfcAdapter;
    private boolean mAndroidBeamAvaible = false;
    private PackageManager packageManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        packageManager = getActivity().getPackageManager();
        //Controllo la disponibiltà dell'nfc
        checkNfc();
        //Controllo la connessione a Internet
        checkInternetConnection();
        //Creazione NFC Adptr
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        //metodo per il richiamo del sender messaggio NDEF
        nfcAdapter.setNdefPushMessageCallback(this, getActivity());
        //metodo per il richiamo del metodo da eseguire a trasmissione completata
        nfcAdapter.setOnNdefPushCompleteCallback(this, getActivity());
    }

    /**
     * Metodo di test per la connessione dati. Se non vi è connessione, il fragment viene riavviato finchè essa non viene stabilita
     */
    private void checkInternetConnection() {
        boolean connected = ConnectionUtil.checkInternetConn(this.getActivity());
        if (!connected)
            showDialog(this.getActivity(), getString(R.string.no_connection_title), getString(R.string.connection_disabled));
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
     * Metodo che controlla sato nFC
     */
    private void checkNfc() {
        //Controllo disp del chip nfc
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            Toast.makeText(getContext(), R.string.no_chip_nfc, Toast.LENGTH_SHORT).show();
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mAndroidBeamAvaible = false;
        } else {
            nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
            //Verifico che l'NFC sia abilitato, se no apre le impostazioni per consentirne l'attivazione
            if (!nfcAdapter.isEnabled()) {
                Toast.makeText(getContext(), R.string.no_NFC_enabled, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            }
            //Verifica che Android Beam sia abilitato, se no apre le impostazioni per consentirne l'attivazione
            if (!nfcAdapter.isNdefPushEnabled()) {
                Toast.makeText(getContext(), R.string.no_beam_enabled, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
            }
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
        username = prefs.getInt(Keys.ID_UTENTE, -1);
        String request;
        request = Keys.RENT + ":" + username;
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
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
