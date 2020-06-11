package it.uniba.di.gruppo17;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import it.uniba.di.gruppo17.asynchttp.AsyncCheckConnection;
import it.uniba.di.gruppo17.asynchttp.AsyncSignUp;
import it.uniba.di.gruppo17.util.*;

import static it.uniba.di.gruppo17.util.Keys.EMAIL;

/**
 * @author Pasquale , Andrea Montemurro
 * Activity di registrazione alla piattaforma
 */
public class SignUpActivity extends AppCompatActivity {

    /*
     * Regex per controllo password
     */
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");


    /*
     * EdiText del form di registrazione
     */
    private EditText ETemail, ETname, ETsurname, ETpassword;
    private Button BTSignUp;

    private AsyncCheckConnection mCheckInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ETemail = (EditText) findViewById(R.id.email_sign_up);
        ETname = (EditText) findViewById(R.id.name_sign_up);
        ETsurname = (EditText) findViewById(R.id.surname_sign_up);
        ETpassword = (EditText) findViewById(R.id.password_sign_up);

        BTSignUp = (Button) findViewById(R.id.button_sign_up);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCheckInternet = new AsyncCheckConnection(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Verifico che il server risponde
        try
        {
            if (!mCheckInternet.execute().get())
            {
                //Se non risponde dopo un timeout di 2,5 sec stampo un messaggio
                Toast.makeText(SignUpActivity.this, R.string.no_server_response, Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        if (BTSignUp != null)
        {
            BTSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = ETname.getText().toString();
                    String surname = ETsurname.getText().toString();
                    String email = ETemail.getText().toString();
                    String password = ETpassword.getText().toString();

                    /*Controllo  email valida*/
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    {
                        /*Controllo se password valida*/
                        if (PASSWORD_PATTERN.matcher(password).matches())
                        {
                            if (ConnectionUtil.checkInternetConn(SignUpActivity.this))
                            {
                                /*Eseguo signup*/
                                try {
                                    if (signUp(email, name, surname, password))
                                    {
                                        goToLogin();  /*Avvenuto con successo, passo a schermata login*/
                                    }
                                    else
                                    {
                                        /*Errore email già usata (chiave nella tabella db, segnalo*/
                                        alreadyUsedEmail(ETemail);
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                connectionError(); /*Mancanza di connessione, segnalo errore*/
                            }
                        }
                        else
                        {
                            invalidPassword(ETpassword); /*Formato password non valido*/
                        }
                    }else
                    {
                        invalidEmail(ETemail); /*Formato Email non valido*/
                    }

                }
            });
        }
    }

    /**
     * Funzione che evidenzia errore di input nell'editText email
     * @param ETemail Edit text email
     */
    private void invalidEmail(EditText ETemail) {
        ETemail.setText("");
        ETemail.setHintTextColor(Color.RED);
        ETemail.setHint(R.string.not_valid_email_address);
    }


    /**
     * Funzione che evidenzia errore di input nell'editText password
     * @param ETpassword Edit text password
     */
    private void invalidPassword(EditText ETpassword) {
        ETpassword.setText("");
        ETpassword.setHintTextColor(Color.RED);
        ETpassword.setHint(R.string.not_valid_password);
        Toast.makeText(getApplicationContext(), R.string.toast_password, Toast.LENGTH_LONG).show();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.password_sign_up).getWindowToken(), 0);
    }


    /**
     * Funzione per effettuare il sign up di un nuovo utente.
     *
     * @param email    email del nuovo utente.
     * @param name     nome del nuovo utente.
     * @param surname  cognome del nuovo utente.
     * @param password password del nuovo utente.
     * @return true se l'operazione è andata a buon fine, false altrimenti.
     */
    private boolean signUp(String email, String name, String surname, String password) throws UnsupportedEncodingException {
        boolean result = false;
        String str = Keys.SERVER
                .concat("add_utente.php?pw=").concat(URLEncoder.encode(password, String.valueOf(StandardCharsets.UTF_8)))
                .concat("&nome=").concat(URLEncoder.encode(name, String.valueOf(StandardCharsets.UTF_8)))
                .concat("&cognome=").concat(URLEncoder.encode(surname, String.valueOf(StandardCharsets.UTF_8)))
                .concat("&email=").concat(URLEncoder.encode(email, String.valueOf(StandardCharsets.UTF_8)));
        try {
            URL url = new URL(str);
            AsyncSignUp signUp = new AsyncSignUp();
            result = signUp.execute(url).get();
        } catch (MalformedURLException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * Funzione che l'email scelta è gia in uso
     */
    private void alreadyUsedEmail(EditText ETemail)
    {
        ETemail.setText("");
        ETemail.setHintTextColor(Color.RED);
        ETemail.setHint(R.string.email_already_used);
    }

    /**
     * Funzione che torna a login dopo la registrazione passando all'activity la email di registrazione
     */
    private void goToLogin()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(EMAIL, ETemail.getText().toString());
        startActivity(intent);
        finish();
    }


    /**
     * Go back to the LoginActivity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Funzione che segnala l'assenza di connessione alla rete
     */
    private void connectionError() {
        new AlertDialog.Builder(SignUpActivity.this)
                .setTitle(R.string.no_connection_title)
                .setMessage(R.string.no_connection_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }


}
