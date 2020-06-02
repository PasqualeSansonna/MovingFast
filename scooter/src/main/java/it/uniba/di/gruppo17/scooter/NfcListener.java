package it.uniba.di.gruppo17.scooter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;

import android.nfc.NfcEvent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * @author Francesco Moramarco
 */

public class NfcListener extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {

    private PendingIntent pendingIntent;
    private  NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_listener);
        //inizializza nfc adapter e definisco Pending intent
        mNfcAdapter =  NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    public boolean checkScooter()
    {
        boolean ok;
        int batteria = getBatteryPercentage(this);
        String SERVER = Keys.SERVER + "checkmonopattino.php?id="+Keys.SCOOTER_ID +"&lat="+LocationService.realTimeDeviceLocation().getLatitude()+
                "&long="+LocationService.realTimeDeviceLocation().getLongitude()+"&bat="+batteria;
        URL url = null;
        try {
            url = new URL(SERVER);
            AsyncCheckMonopattino checkMonopattino = new AsyncCheckMonopattino();
            ok = checkMonopattino.execute(url).get();
        } catch (MalformedURLException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            Log.d("Qui","Errore");
            ok = false;
        }
        return ok;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        CheckInternetConnection mCheckInternetConnection = new CheckInternetConnection(this);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    && mNfcAdapter!=null && mNfcAdapter.isEnabled()
                    && mCheckInternetConnection.execute().get() )
            {
                mNfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
                //metodo per il richiamo del sender messaggio NDEF
                mNfcAdapter.setNdefPushMessageCallback(this, this);
                //metodo per il richiamo del metodo da eseguire a trasmissione completata
                mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
                //Per dare il tempo al servizio di localizazione di partire...
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run()
                    {
                        checkScooter();
                    }
                },7000);
            }
            else
            {
                startActivity(new Intent (this, MainActivity.class));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if ( mNfcAdapter != null ) //disabilito il foregound dispatch
            mNfcAdapter.disableForegroundDispatch(this);
    }

    //viene chiamato quando l'nfc rileva una nfc card o tag.
    // VEDIAMO qual è l'azione
    //se l'azione è ACTION_TAG_DISCOVERED, ACTION_TECH_DISCOVERED or ACTION_NDEF_DISCOVERED
    //allora ottieno il dato contenuto nell'intent, usando  getParcelableArrayExtra con parametro EXTRA_NDEF_MESSAGES
    //Se EXTRA_NDEF_MESSAGES non è nullo, creiamo l'oggetto NdefMessage da questi dati
    //Altrimenti cerchiamo di ottenere i dati dal content EXTRA_ID dall'intent chiamando getByteArrayExtra
    //Otteniamo anche il Tag object. Poi chiamo dumpTagData con parametro questa istanza di tag.
    //Poi creiamo NdefRecord con i dati ricevuti. poi creiamo NdefMessage
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            //NdefMessage[] message;
            NdefMessage mNdefMessage;
            if (rawMessages != null) {
                /* message = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    message[i] = (NdefMessage) rawMessages[i];
                }*/
                mNdefMessage = (NdefMessage)rawMessages[0];
                NdefRecord[] mNdefRecord  = mNdefMessage.getRecords();
                String receivedData = new String( mNdefRecord[0].getPayload() );
                String[] data = receivedData.split(":");
                String requestedOperation = data[Keys.ACTION];
                switch (requestedOperation)
                {
                    case Keys.RENT: //noleggio
                        startRent(data);
                        break;
                    case Keys.CLOSE_RENT: //chiudi noleggio
                        closeRent(data);
                        break;
                    default:
                        Log.d("Codice operazione","Codice operazione strano");
                        return;
                }
            }
            else
                Toast.makeText(this,R.string.nfcBumpNoData_message,Toast.LENGTH_LONG).show();
        }
    }

    private void startRent(String[] data)
    {
        Calendar mCalendar = Calendar.getInstance();
        SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = mDateFormat.format(mCalendar.getTime());
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        int idUtente = Integer.parseInt(data[Keys.USER_ID]);
        int rentId = 0;
        if ( idUtente >= 0 )
        {
            String server = Keys.SERVER + "rent.php?idU="+idUtente+"&idM="+Keys.SCOOTER_ID +"&data="+date+
                    "&ora="+time+"&lat="+LocationService.realTimeDeviceLocation().getLatitude()+"&long="+LocationService.realTimeDeviceLocation().getLongitude();
            try {
                URL url = new URL(server);
                AsyncRentScooter mRent = new AsyncRentScooter();
                rentId = mRent.execute(url).get();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if ( rentId < 0 )
                Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show();
        }
    }

    private void closeRent(String[] data)
    {
        Calendar mCalendar = Calendar.getInstance();
        SimpleDateFormat mDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = mDateFormat.format(mCalendar.getTime());
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        int idUtente = Integer.parseInt(data[Keys.USER_ID]);
        int idNoleggio = Integer.parseInt(data[Keys.RENT_ID]);
        if ( idUtente > -1 && idNoleggio > -1 )
        {
            String server = Keys.SERVER + "close_rent.php?idU="+idUtente+"&idM="+Keys.SCOOTER_ID +"&idN="+idNoleggio+"&data="+date+
                    "&ora="+time+"&lat="+LocationService.realTimeDeviceLocation().getLatitude()+"&long="+LocationService.realTimeDeviceLocation().getLongitude();
            try{
                URL url = new URL(server);
                AsyncCloseRentFragment mCloseRent = new AsyncCloseRentFragment();
                if ( mCloseRent.execute(url).get() )
                    Toast.makeText(this, R.string.rentClosedSuccessfully_message,Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this,R.string.rentClosedError_message,Toast.LENGTH_LONG).show();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    //Per ottenere la batteria corrente del monopattino
    private static int getBatteryPercentage(Context context) {

        if (Build.VERSION.SDK_INT >= 21) {

            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        } else {

            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;

            return (int) (batteryPct * 100);
        }
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String sendId = String.valueOf(Keys.SCOOTER_ID);
        NdefRecord ndefRecord = NdefRecord.createMime("text/plain", sendId.getBytes());
        NdefMessage ndefMessage = new NdefMessage(ndefRecord);
        return ndefMessage;
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
    }
}