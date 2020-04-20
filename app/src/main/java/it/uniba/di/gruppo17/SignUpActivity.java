package it.uniba.di.gruppo17;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

                    /**Check if the email address pattern is correct*/
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    {
                        if (signup(name, surname, email, password))
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
     * Funzione che registra l'utente inserendo le credenziali nel DB
     *
     * @param name nome utente
     * @param surname cognome utente
     * @param email email utente
     * @param password password utente
     * @return
     */
    private boolean signup(String name, String surname, String email, String password)
    {
        boolean result = false;
        // TODO: 20/04/2020 implement signup method
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
        // TODO: 20/04/2020 Use constant string here
        intent.putExtra("email", ETemail.getText().toString());
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
