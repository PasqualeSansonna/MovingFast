package it.uniba.di.gruppo17;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import it.uniba.di.gruppo17.asynchttp.AsyncLogin;
import it.uniba.di.gruppo17.util.ConnectionUtil;
import it.uniba.di.gruppo17.util.Keys;

import static it.uniba.di.gruppo17.util.Keys.EMAIL;
import static it.uniba.di.gruppo17.util.Keys.PASSWORD;

/**
 * @author Pasquale, Andrea Montemurro
 * Activity di Login alla piattaforma
 */
public class LoginActivity extends AppCompatActivity {
    /**
     * EdiText campi per email e password
     */
    private EditText ETemail, ETpassword;
    /**
     * Sentinella per la memorizzazione delle credentials.
     */
    private boolean saveCredential = false;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        ETemail = (EditText) findViewById(R.id.email_login);
        ETpassword = (EditText) findViewById(R.id.password_login);

        /**
         * Controllo se è presente un'email
         * passata dalla SingUpActivity andata a buon fine
         * ed eventualmente la inserisco nella ETemail,
         * avvio il dialog chiudendo la tastiera
         */
        Bundle dati_passati = getIntent().getExtras();
        if (dati_passati != null) {
            ETemail.setText(dati_passati.getString(EMAIL));
            SignUpDialog signUpDialog = new SignUpDialog(LoginActivity.this);
            signUpDialog.startDialog();
        }
        /** Controllo delle sharedPref per eventuali credenziali salvate*/
        preferences = getSharedPreferences("MovingFastPreferences", Context.MODE_PRIVATE);
        if (preferences.contains(EMAIL) && preferences.contains(PASSWORD)) {
            /**Bisogna controllare la connessione, se c'è è possibile fare il login*/
            if (ConnectionUtil.checkInternetConn(LoginActivity.this))
            {
                login();
            }
            else
            {
                connectionError();
            }

        }

        /**Switch per salvataggio credenziali*/
        Switch switchCredenziali = (Switch) findViewById(R.id.switch_login);
        if (switchCredenziali != null)
        {
            switchCredenziali.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    saveCredential = isChecked;
                }
            });
        }

        /** Bottoni per Login e Registrazione */
        Button BTlogin = (Button) findViewById(R.id.login_button);
        Button BTSignUp = (Button) findViewById(R.id.button_sign_up);

        if (BTSignUp != null) /** Ci si intende registrarew*/
        {
            BTSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSignup();
                }
            });
        }

        if (BTlogin != null) /** Ci si intende loggare*/
        {
            BTlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ConnectionUtil.checkInternetConn(LoginActivity.this)) {
                        String email = ETemail.getText().toString();
                        String password = ETpassword.getText().toString();
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) /** Check the email address pattern*/
                        {
                            if (checkValues(email, password)) {
                                if (isChecked())
                                    writePreferences(email, password);
                                login();
                            } else {
                                wrongCredentials();
                            }
                        }
                        else
                        {
                            wrongEmailAddress();
                        }

                    } else {
                        connectionError();
                    }
                }
            });
        }

    }

    /**
     * Funzione che segnala l'assenza di connessione alla rete
     */
    private void connectionError() {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(R.string.no_connection_title)
                .setMessage(R.string.no_connection_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }


    /**
     * Scrive nelle SharedPreferences email e password in maniera tale da rendere automatico il login al prossimo accesso.
     *
     * @param email    email dell'utente.
     * @param password password dell'utente.
     */
    private void writePreferences(String email, String password) {
        if (!(preferences.contains(EMAIL)) && !(preferences.contains(Keys.PASSWORD))) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(EMAIL, email);
            editor.putString(PASSWORD, password);
            editor.apply();
        }
    }


    /**
     * Funzione che imposta messaggi di errore nelle editText delle credenziali se immesse in modo errato
     */
    private void wrongCredentials()
    {
        ETemail.setText("");
        ETemail.setHint(R.string.wrong_login_email);
        ETemail.setHintTextColor(Color.RED);
        ETpassword.setText("");
        ETpassword.setHint(R.string.wrong_login_password);
        ETpassword.setHintTextColor(Color.RED);
    }

    /**
     * Funzione che imposta messaggio d'errore nella editText della mail se non è inserito un indirizzo valido
     */
    private void wrongEmailAddress() {
        ETemail.setText("");
        ETemail.setHint(R.string.not_valid_email_address);
        ETemail.setHintTextColor(Color.RED);
    }


    /**
     * Funzione che controlla le credenziali per login
     * @param email indirizzo mail inserito
     * @param password password inserita
     * @return true se credenziali corrette, false credenziali sbagliate
     */
    private boolean checkValues(String email, String password)
    {
        boolean checked = false;
        if (!email.equals("") && !password.equals(""))
        {
            String str = Keys.SERVER + "login.php?email=" + email + "&pw=" + password;
            try {
                SharedPreferences.Editor editor = preferences.edit();
                URL url = new URL(str);

                    AsyncLogin utente = new AsyncLogin();
                    int id = utente.execute(url).get();
                    editor.putInt(Keys.ID_UTENTE, id);
                    editor.apply();
                /** controllo se l'id utente nelle shared preferences è errato (-1),
                 *  nel caso in cui è errato lo rimuovo e restituisco false
                 *  altrimenti true (il suo id resta salvato nelle shared preferences)
                 *  NB Per controllo shared preferences
                 *  device file explorer> data> ns package> shared_prefs> MovingFastPreferences.xml
                 */
                if (preferences.getInt(Keys.ID_UTENTE, -1) == -1) {
                        checked = false;
                        editor.remove(Keys.ID_UTENTE);
                    }else {
                        checked = true;
                    }
            } catch (MalformedURLException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return checked;
    }


    /**
     * Funzione che avvia activity per Registrazione
     */
    private void startSignup ()
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Login effettuato -> passaggio all'act principale
     */
    private void login()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Metodo che verifica che lo switch sia attivo o meno.
     * @return true se lo switch è attivo, false altrimenti.
     */
    private boolean isChecked() {
        return saveCredential;
    }
    
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
