package it.uniba.di.gruppo17;

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
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import java.util.concurrent.ExecutionException;

import it.uniba.di.gruppo17.asynchttp.AsyncCheckConnection;
import it.uniba.di.gruppo17.util.Keys;

/**
 * @author Francesco Moramarco
 */
public class CloseRentFragment extends Fragment implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback
{
    public static SharedPreferences prefs;
    private NfcAdapter nfcAdapter;
    private AsyncCheckConnection mCheckConnection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getActivity().getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_close_rent, container, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mCheckConnection = new AsyncCheckConnection(getContext());
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        if (deviceNfcIsOn() && androidBeamIsOn()) {
            try {
                if ( mCheckConnection.execute().get() ) {
                    //metodo per il richiamo del sender messaggio NDEF
                    nfcAdapter.setNdefPushMessageCallback(this, getActivity());
                    //metodo per il richiamo del metodo da eseguire a trasmissione completata
                    nfcAdapter.setOnNdefPushCompleteCallback(this, getActivity());
                } else {
                    new AlertDialog.Builder(getContext())
                            .setCancelable(false)
                            .setTitle("Connection Error")
                            .setMessage("Please check your internet connection. Then if still not working the server is down. Please reload")
                            .setPositiveButton("reload", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Fragment realodCloseRent = new CloseRentFragment();
                                    getFragmentManager().beginTransaction().detach(CloseRentFragment.this).attach(realodCloseRent).commit();
                                }
                            })
                            .create()
                            .show();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Crea il messaggio da inviare tramite NFC. Il messaggio è composto dall'operazione richiesta (chiusura in questo caso)
     * dall'id dell'utente, del monopattino e del noleggio
     * @param nfcEvent
     * @return ndefMessage
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
        int userId = prefs.getInt(Keys.USER_ID,-1);
        int rentId = prefs.getInt(Keys.RENT_ID, -1);
        String request = Keys.CLOSE + ":" + userId + ":" + rentId;
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", request.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }

    /**
     * Metodo eseguito subito dopo che il bump è stato eseguito
     * @param nfcEvent
     */
    @Override
    public void onNdefPushComplete(NfcEvent nfcEvent) {
        getFragmentManager().popBackStack();
        Fragment toResultCloseRentFragment = new ResultCloseRentFragment();
        getFragmentManager().beginTransaction().replace(R.id.id_fragment_close_rent,toResultCloseRentFragment).commit();
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
            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
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
            new AlertDialog.Builder(getContext())
                    .setCancelable(false)
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
}
