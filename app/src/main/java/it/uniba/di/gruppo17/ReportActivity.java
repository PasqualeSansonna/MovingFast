package it.uniba.di.gruppo17;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import it.uniba.di.gruppo17.asynchttp.JsonFromHttp;
import it.uniba.di.gruppo17.util.ConnectionUtil;
import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.Reporting;

/**
 * @author francesco moramarco
 */
public class ReportActivity extends AppCompatActivity {

    private BottomSheetDialog mBottomSheetDialog;
    private PendingIntent pendingIntent;
    private NfcAdapter mNfcAdapter;
    private AlertDialog mAlertDialog;

    private SharedPreferences prefs;
    private CheckBox brakesCheckBox;
    private CheckBox wheelsCheckBox;
    private CheckBox handlebarsCheckBox;
    private CheckBox acceleratorCheckBox;
    private CheckBox lockCheckBox;
    private CheckBox otherCheckBox;
    private Button reportButton;
    private EditText idScooter;
    private Reporting report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        Toolbar toolbar = findViewById(R.id.toolbarReportActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.reportingActvityName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = this.getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        mNfcAdapter =  NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);

        brakesCheckBox = findViewById(R.id.brakesCheckBox);
        wheelsCheckBox = findViewById(R.id.wheelsCheckBox);
        handlebarsCheckBox = findViewById(R.id.handlebarCheckBox);
        acceleratorCheckBox = findViewById(R.id.acceleratorCheckBox);
        lockCheckBox = findViewById(R.id.lockCheckBox);
        otherCheckBox = findViewById(R.id.otherCheckBox);
        reportButton = findViewById(R.id.buttonConfirmReport);
        idScooter = findViewById(R.id.idScooter);
        idScooter.setInputType(InputType.TYPE_NULL);
        idScooter.setFocusable(false);
        idScooter.setClickable(true);
        idScooter.setTextIsSelectable(true);

        mBottomSheetDialog = new BottomSheetDialog( this, R.style.BottomSheetDialogTheme );
        mBottomSheetDialog.setContentView(R.layout.bottomsheet_report_activity);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater mInflater = this.getLayoutInflater();
        builder.setView(mInflater.inflate(R.layout.loading_dialog_layout,null));
        builder.setCancelable(false);
        mAlertDialog = builder.create();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if ( mNfcAdapter != null ) //disabilito il foregound dispatch
            mNfcAdapter.disableForegroundDispatch(this);

        idScooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( deviceHasNfc() && deviceNfcIsOn() && androidBeamIsOn() )
                {
                    mBottomSheetDialog.show();
                    mNfcAdapter.enableForegroundDispatch( ReportActivity.this, pendingIntent, null, null);
                }
                else
                    return;
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( !idScooter.getText().toString().equals("") )
                    report = new Reporting(Integer.parseInt(idScooter.getText().toString()), 0,
                        prefs.getInt(Keys.USER_ID, 0), 0, 0, 0, 0, 0, 0);
                else
                {
                    Snackbar.make(v,R.string.reportScooterIdError,Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (brakesCheckBox.isChecked()) {
                    report.setBrakes(1);
                }
                if (wheelsCheckBox.isChecked()) {
                    report.setWheels(1);
                }
                if (handlebarsCheckBox.isChecked()) {
                    report.setHandlebars(1);
                }
                if (acceleratorCheckBox.isChecked()) {
                    report.setAccelerator(1);
                }
                if (lockCheckBox.isChecked()) {
                    report.setLock(1);
                }
                if (otherCheckBox.isChecked()) {
                    report.setOther(1);
                }

                String server = Keys.SERVER + "add_segnalazione.php?id_utente="+report.getIdUser()+"&id_monopattino="+report.getIdScooter()
                        +"&guasto_freni="+report.isBrakesBroken()+"&guasto_ruote="+report.isWheelsBroken()
                        +"&guasto_manubrio="+report.isHandlebarsBroken()+ "&guasto_acceleratore="+report.isAcceleratorBroken()
                        + "&guasto_blocco="+report.isLockBroken()+ "&guasto_altro="+report.isOtherBroken();
                try {
                    URL url = new URL(server);
                    AsyncAddReport asyncAddReport = new AsyncAddReport();
                    if ( ConnectionUtil.checkInternetConn(ReportActivity.this) )
                        asyncAddReport.execute(url);
                    else
                        Snackbar.make(v,R.string.networkConnectionError_tile,
                                Snackbar.LENGTH_INDEFINITE).setAction(R.string.snackbar_action, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ReportActivity.this.recreate();
                            }
                        }).show();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void afterTask( Boolean result )
    {
        if (result)
            new AlertDialog.Builder(this)
                    .setTitle(R.string.reportCompleted_title)
                    .setMessage(R.string.reportCompleted_text)
                    .create().show();
        else
            new AlertDialog.Builder(this)
                    .setTitle(R.string.reportFailed_title)
                    .setMessage(R.string.reportFailed_text)
                    .create().show();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TAG_DISCOVERED.equals(action))
        {
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage mNdefMessage;
            if (rawMessages != null)
            {
                mNdefMessage = (NdefMessage)rawMessages[0];
                NdefRecord[] mNdefRecord  = mNdefMessage.getRecords();
                String receivedData = new String( mNdefRecord[0].getPayload() );
                idScooter.setText(receivedData);
            }
            else
                Toast.makeText(this,R.string.nfcBumpNoDataReceived,Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(this,R.string.nfcBumpFailed,Toast.LENGTH_LONG).show();
        mBottomSheetDialog.dismiss();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if ( mNfcAdapter != null ) //disabilito il foregound dispatch
            mNfcAdapter.disableForegroundDispatch(this);
    }

    /**
     * Metodo che controlla se il dispotivo è dotato di chip NFC
     * @return true se chip nfc è presente, false altrimenti
     */
    private boolean deviceHasNfc() {
        if (mNfcAdapter == null) {
            new android.app.AlertDialog.Builder(this)
                    .setTitle(R.string.noNfc_title)
                    .setCancelable(false)
                    .setMessage(R.string.noNfc_message)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
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
        if ( mNfcAdapter.isEnabled() )
            return true;
        else
        {
            new android.app.AlertDialog.Builder(this)
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
        if ( mNfcAdapter.isNdefPushEnabled() )
            return true;
        else
        {
            new android.app.AlertDialog.Builder(this)
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

    private class AsyncAddReport extends AsyncTask<URL, Void, Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mAlertDialog.show();
        }

        @Override
        protected Boolean doInBackground(URL... params) {

            boolean result = false;
            for (URL param : params) {
                try {
                    JSONObject json = new JsonFromHttp().getJsonObject(param);
                    if (json.getString("ok").equals("true"))
                        result = true;
                } catch (Exception e) {
                    result = false;
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            mAlertDialog.dismiss();
            super.onPostExecute(aBoolean);
            afterTask(aBoolean);
        }
    }
}
