package it.uniba.di.gruppo17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class LoginActivity extends AppCompatActivity {
    /**
     * EdiText campi per email e password
     */
    private EditText ETemail, ETpassword;

    private boolean saveCredentials;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ETemail = (EditText) findViewById(R.id.email_login);
        ETpassword = (EditText) findViewById(R.id.password_login);
        /** Controllo delle sharedPref per eventuali credenziali salvate*/
        /* TODO: 18/04/2020 USA costanti STRINGHE qui */
        preferences = getSharedPreferences("MovingFastPreferences", Context.MODE_PRIVATE);
        if (preferences.contains("email") && preferences.contains("password")) {
            login();
        }

        /**Switch per salvataggio credenziali*/
        Switch switchCredenziali = (Switch) findViewById(R.id.switch_login);
        if (switchCredenziali != null)
        {
            switchCredenziali.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    saveCredentials = isChecked;
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
                    /* TODO: 18/04/2020 have to the check internet connection here */
                    String email = ETemail.getText().toString();
                    String password = ETpassword.getText().toString();
                    /** Controllo credenziali e se sono giuste si fa login*/
                    if (checkValues(email, password))
                    {
                        login();
                        if (saveCredentials)
                            writeCredentialsInPreferences(email, password);
                    }
                    else
                    {
                        wrongCredentials();
                    }
                }
            });
        }

    }


    /**
     * Funzione che scrive credenziali nelle Shared Preferences
     * @param email
     * @param password
     */
    private void writeCredentialsInPreferences(String email, String password)
    {
        /* TODO: 18/04/2020 USE STRING CONSTants */
        if (!preferences.contains("email")  && !preferences.contains("password"))
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("email", email);
            editor.putString("password", password);
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
     * Funzione che controlla le credenziali per login
     * @param email indirizzo mail inserito
     * @param password password inserita
     * @return true se credenziali corrette, false credenziali sbagliate
     */
    private boolean checkValues(String email, String password)
    {
        boolean checked = false;
        if (!email.equals("") || !password.equals(" "))
        {
            /* TODO: 18/04/2020 have to check credentials */
            
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



    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
