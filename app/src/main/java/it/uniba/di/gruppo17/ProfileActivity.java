package it.uniba.di.gruppo17;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.*;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewName, textViewSurname, textViewEmail;
    private TextView textViewName_Surname;
    private Button buttonEditProfile, buttonChangePassword, buttonViewPastRentals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textViewName = (TextView) findViewById(R.id.textView_name);
        textViewSurname = (TextView) findViewById(R.id.textView_surname);
        textViewEmail = (TextView) findViewById(R.id.textView_email);
        textViewName_Surname = (TextView) findViewById(R.id.textView_name_surname);
        buttonEditProfile = (Button) findViewById(R.id.button_editProfile);
        buttonChangePassword = (Button) findViewById(R.id.button_changePassword);
        buttonViewPastRentals = (Button) findViewById(R.id.button_viewPastRentals);

    }
}
