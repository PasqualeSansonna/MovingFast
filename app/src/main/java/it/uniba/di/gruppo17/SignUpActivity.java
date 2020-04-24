package it.uniba.di.gruppo17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import it.uniba.di.gruppo17.asynhttp.AsyncSignUp;
import it.uniba.di.gruppo17.util.Keys;

import static it.uniba.di.gruppo17.util.Keys.EMAIL;

/**
 * @author Pasquale , Andrea Montemurro
 * Activity di registrazione alla piattaforma
 */
public class SignUpActivity extends AppCompatActivity {
    /**
     * EdiText del form di registrazione
     */
    EditText ETemail, ETname, ETsurname, ETpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ETemail = (EditText) findViewById(R.id.email_sign_up);
        ETname = (EditText) findViewById(R.id.name_sign_up);
        ETsurname = (EditText) findViewById(R.id.surname_sign_up);
        ETpassword = (EditText) findViewById(R.id.password_sign_up);

        Button BTSignUp = (Button) findViewById(R.id.button_sign_up);
        if (BTSignUp != null)
        {
            BTSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = ETname.getText().toString();
                    String surname = ETsurname.getText().toString();
                    String email = ETemail.getText().toString();
                    String password = ETpassword.getText().toString();

                    /**Controllo se campi email corretti*/
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    {
                        if (signUp(email, name, surname, password))
                        {
                            goToLogin();
                        }
                        else
                        {
                            wrongUserInfo();
                        }
                    }else
                    {
                        ETemail.setText("");
                        ETemail.setHintTextColor(Color.RED);
                        ETemail.setHint(R.string.email_input_error);
                    }

                }
            });
        }
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
    private boolean signUp(String email, String name, String surname, String password) {
        boolean result = false;
        String str = Keys.SERVER
                .concat("add_utente.php?pw=").concat(password)
                .concat("&nome=").concat(name)
                .concat("&cognome=").concat(surname)
                .concat("&email=").concat(email);
        try {
            URL url = new URL(str);
            AsyncSignUp signUp = new AsyncSignUp();
            result = signUp.execute(url).get();
            Log.d("CHECK", String.valueOf(result));
        } catch (MalformedURLException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * Funzione che evidenzia errori di input per le credenziali
     */
    private void wrongUserInfo()
    {
        ETemail.setText("");
        ETname.setText("");
        ETsurname.setText("");
        ETpassword.setText("");
        ETemail.setHintTextColor(Color.RED);
        ETemail.setHint(R.string.email_input_error);
        ETname.setHintTextColor(Color.RED);
        ETname.setHint(R.string.name_input_error);
        ETsurname.setHintTextColor(Color.RED);
        ETsurname.setHint(R.string.surname_input_error);
        ETpassword.setHintTextColor(Color.RED);
        ETpassword.setHint(R.string.password_input_error);
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

}
