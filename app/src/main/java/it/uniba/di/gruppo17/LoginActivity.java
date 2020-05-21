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
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import it.uniba.di.gruppo17.asynchttp.AsyncCheckConnection;
import it.uniba.di.gruppo17.asynchttp.AsyncLogin;
import it.uniba.di.gruppo17.util.ConnectionUtil;
import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.UserProfile;

import static it.uniba.di.gruppo17.util.Keys.EMAIL;
import static it.uniba.di.gruppo17.util.Keys.PASSWORD;

/**
 * @author Pasquale, Andrea Montemurro
 * Activity di Login alla piattaforma
 */
public class LoginActivity extends AppCompatActivity {
    /*
     * EdiText campi per email e password
     */
    private EditText ETemail, ETpassword;
    private Button BTlogin, BTSignUp;
    private boolean isManutentore = false;
    // Sentinella per la memorizzazione delle credentials.
    private boolean saveCredential = false;
    private SharedPreferences preferences;

    private AsyncCheckConnection mCheckInternet;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /* Campi txt email e password*/
        ETemail = (EditText) findViewById(R.id.email_login);
        ETpassword = (EditText) findViewById(R.id.password_login);
        /* Bottoni per Login e Registrazione */
        BTlogin = (Button) findViewById(R.id.login_button);
        BTSignUp = (Button) findViewById(R.id.button_sign_up);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCheckInternet = new AsyncCheckConnection(this);
    }

    @Override
    protected void onResume() {
        /*
         * Controllo se è presente un'email
         * passata dalla SingUpActivity andata a buon fine
         * ed eventualmente la inserisco nella ETemail,
         * avvio il dialog chiudendo la tastiera
         */


        //Verifico che il server risponde
        try
        {
            if (!mCheckInternet.execute().get())
            {
                //Se non risponde dopo un timeout di 2,5 sec stampo un messaggio
                Toast.makeText(LoginActivity.this, R.string.no_server_response, Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Bundle dataFromSignup = getIntent().getExtras();
        if (dataFromSignup != null) {
            ETemail.setText(dataFromSignup.getString(EMAIL));
            SignUpDialog signUpDialog = new SignUpDialog(LoginActivity.this);
            signUpDialog.startDialog();
        }
        /* Controllo delle sharedPref per eventuali credenziali salvate*/
        preferences = getSharedPreferences("MovingFastPreferences", Context.MODE_PRIVATE);
        if (preferences.contains(EMAIL) && preferences.contains(PASSWORD)) {
            /*Bisogna controllare la connessione, se c'è è possibile fare il login*/
            if (ConnectionUtil.checkInternetConn(LoginActivity.this))
            {
                login();
            }
            else
            {
                connectionError();
            }

        }

        /*Switch per salvataggio credenziali*/
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



        if (BTSignUp != null) /* Ci si intende registrarew*/
        {
            BTSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSignup();
                }
            });
        }

        if (BTlogin != null) /* Ci si intende loggare*/
        {
            BTlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ConnectionUtil.checkInternetConn(LoginActivity.this)) {
                        String email = ETemail.getText().toString();
                        String password = ETpassword.getText().toString();
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) /* Check the email address pattern*/
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
        super.onResume();
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

                UserProfile loggedUser = null;

                AsyncLogin utente = new AsyncLogin();
                loggedUser = utente.execute(url).get();
                editor.putInt(Keys.USER_ID, loggedUser.getId());
                //salvo in shared tipo utente
                editor.putBoolean(Keys.USER_TYPE, loggedUser.isManutentore());
                editor.apply();
                /** controllo se l'id utente nelle shared preferences è errato (-1),
                 *  nel caso in cui è errato lo rimuovo e restituisco false
                 *  altrimenti true (il suo id resta salvato nelle shared preferences)
                 *  NB Per controllo shared preferences
                 *  device file explorer> data> ns package> shared_prefs> MovingFastPreferences.xml
                 */
                if (preferences.getInt(Keys.USER_ID, -1) == -1) {
                        checked = false;
                        editor.remove(Keys.USER_ID);
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
        if (!preferences.getBoolean(Keys.USER_TYPE, false))
        {   //Caso utente fruitore
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
        {   //Caso utente manutentore
            Intent intent = new Intent(this, MainMaintainerActivity.class);
            startActivity(intent);
        }
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
