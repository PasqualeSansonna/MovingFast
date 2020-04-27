package it.uniba.di.gruppo17;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.net.MalformedURLException;
import java.net.URL;

import it.uniba.di.gruppo17.asynhttp.AsyncLogin;
import it.uniba.di.gruppo17.asynhttp.AsyncViewProfile;
import it.uniba.di.gruppo17.util.ConnectionUtil;
import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.UserProfile;

import static it.uniba.di.gruppo17.util.Keys.ID_UTENTE;


public class ProfileFragment extends Fragment {

    private EditText ET_name;
    private EditText ET_surname;
    private EditText ET_email;
    private Button BT_pastRentals;
    private Button BT_discounts;
    private Button BT_confirmEdit;
    private Button BT_cancelEdit;
    private TableRow R_confirmEdit;
    private TableRow R_cancelEdit;
    private ImageView editProfile;
    SharedPreferences preferences;

    private Integer user_ID;
    private UserProfile user;
    private TextView TV_profileName;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        preferences = context.getSharedPreferences("MovingFastPreferences", Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TV_profileName = (TextView) view.findViewById(R.id.user_profile_name);
        ET_name = (EditText) view.findViewById(R.id.editText_name);
        ET_surname = (EditText) view.findViewById(R.id.editText_surname);
        ET_email = (EditText) view.findViewById(R.id.editText_email);
        BT_pastRentals = (Button) view.findViewById(R.id.buttonPastRentals);
        BT_discounts = (Button) view.findViewById(R.id.buttonDiscount);
        BT_confirmEdit = (Button) view.findViewById(R.id.buttonConfirmEdit);
        BT_cancelEdit = (Button) view.findViewById(R.id.buttonCancel);
        R_confirmEdit = (TableRow) view.findViewById(R.id.rowConfirmEdit);
        R_cancelEdit = (TableRow) view.findViewById(R.id.rowCancel);
        editProfile = (ImageView) view.findViewById(R.id.editProfile);

        ET_name.setEnabled(false);
        ET_surname.setEnabled(false);
        ET_email.setEnabled(false);
        R_confirmEdit.setVisibility(View.GONE);
        R_cancelEdit.setVisibility(View.GONE);

        preferences = getActivity().getSharedPreferences("MovingFastPreferences", Context.MODE_PRIVATE);
        user_ID = Integer.parseInt(preferences.getAll().get(ID_UTENTE).toString());
        String str = Keys.SERVER + "view_profile.php?id=" + user_ID;
        /**Controllo  email valida*/
        try {
                URL url = new URL(str);
                AsyncViewProfile utente = new AsyncViewProfile();
                user = utente.execute(url).get();
                ET_name.setText(user.getNome());
                ET_surname.setText(user.getCognome());
                ET_email.setText(user.getEmail());
                TV_profileName.setText(user.getNome()+ " " + user.getCognome());

            } catch (Exception e) {
                e.printStackTrace();
            }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });

        return view;
    }

    private void editProfile() {
        ET_name.setEnabled(true);
        ET_surname.setEnabled(true);
        ET_email.setEnabled(true);

        BT_pastRentals.setVisibility(View.GONE);
        BT_discounts.setVisibility(View.GONE);
        R_confirmEdit.setVisibility(View.VISIBLE);
        R_cancelEdit.setVisibility(View.VISIBLE);

        BT_confirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ET_name.getText().toString();
                String surname = ET_surname.getText().toString();
                String email = ET_email.getText().toString();

                preferences = getActivity().getSharedPreferences("MovingFastPreferences", Context.MODE_PRIVATE);
                user_ID = Integer.parseInt(preferences.getAll().get(ID_UTENTE).toString());
                String str = Keys.SERVER + "view_profile.php?id_utente=" + user_ID;
                /**Controllo  email valida*/
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    try {
                        URL url = new URL(str);
                        AsyncViewProfile utente = new AsyncViewProfile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else{
                    invalidEmail(ET_email);
                }
            }
        });


    }

    private void invalidEmail(EditText ETemail) {
        ETemail.setText("");
        ETemail.setHintTextColor(Color.RED);
        ETemail.setHint(R.string.not_valid_email_address);
    }

}
