package it.uniba.di.gruppo17;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import it.uniba.di.gruppo17.asynchttp.AsyncEditProfile;
import it.uniba.di.gruppo17.asynchttp.AsyncViewProfile;
import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.UserProfile;

import static it.uniba.di.gruppo17.util.Keys.USER_ID;

/** @author Pasquale, Sgarra
 * Visualuzzazione e Modifica Profilo Utente
 */
public class ProfileFragment extends Fragment {

    private TextView TV_profileName;
    private EditText ET_name;
    private EditText ET_surname;
    private EditText ET_email;
    private Button BT_pastRentals;
    private Button BT_confirmEdit;
    private Button BT_cancelEdit;
    private TableRow R_confirmEdit;
    private TableRow R_cancelEdit;
    private ImageView editProfile;
    SharedPreferences preferences;
    private Integer user_ID;
    private UserProfile user;

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
        view.setBackgroundColor(Color.WHITE);
        TV_profileName = (TextView) view.findViewById(R.id.user_profile_name);
        ET_name = (EditText) view.findViewById(R.id.editText_name);
        ET_surname = (EditText) view.findViewById(R.id.editText_surname);
        ET_email = (EditText) view.findViewById(R.id.editText_email);
        BT_pastRentals = (Button) view.findViewById(R.id.buttonPastRentals);
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

        /**Visualizzo il profilo nelle EditText disabilitate**/
        viewProfile();

        return view;
    }


    private void viewProfile(){

        /**Acquisisco ID dell'utente autenticato per visualizzazarne nome,cognome ed email
         * e creo url connessione
         **/
        preferences = getActivity().getSharedPreferences("MovingFastPreferences", Context.MODE_PRIVATE);
        user_ID = Integer.parseInt(preferences.getAll().get(USER_ID).toString());
        String str = Keys.SERVER + "view_profile.php?id=" + user_ID;

        /**Prendo l'utente passato dall'AsyncViewProfile
         * e stampo le info nelle EditText e TextView
         */
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

            BT_pastRentals.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    Fragment nextFragment = new PastRentalsFragment();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.fragment_container, nextFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.commit();
                }
            });

        /**Cliccando sull'imageView viene abilitata la modifica**/
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfile();
            }
        });
    }

    private void editProfile() {

        ET_name.setEnabled(true);
        ET_surname.setEnabled(true);
        ET_email.setEnabled(true);

        BT_pastRentals.setVisibility(View.GONE);
        editProfile.setVisibility(View.GONE);
        R_confirmEdit.setVisibility(View.VISIBLE);
        R_cancelEdit.setVisibility(View.VISIBLE);

            BT_confirmEdit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    /**result=true se la modifica è andata a buon fine
                     * result=false altrimenti
                     */
                    Boolean result = false;
                    Boolean emptyNameSurnameEmail = false;
                    String name = ET_name.getText().toString();
                    String surname = ET_surname.getText().toString();
                    String email = ET_email.getText().toString();

                    preferences = getActivity().getSharedPreferences("MovingFastPreferences", Context.MODE_PRIVATE);
                    user_ID = Integer.parseInt(preferences.getAll().get(USER_ID).toString());
                    String str = null;
                    try {
                        str = Keys.SERVER + "update_utente.php?id=" + user_ID + "&nome=" + URLEncoder.encode(name, String.valueOf(StandardCharsets.UTF_8)) + "&cognome=" + URLEncoder.encode(surname, String.valueOf(StandardCharsets.UTF_8)) + "&email=" + URLEncoder.encode(email, String.valueOf(StandardCharsets.UTF_8));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                    if (name.matches("")){
                        invalidName(ET_name);
                        emptyNameSurnameEmail = true;
                    }
                    if (surname.matches("")){
                        invalidSurname(ET_surname);
                        emptyNameSurnameEmail = true;
                    }
                    if (email.matches("")){
                        invalidEmail(ET_email);
                        emptyNameSurnameEmail = true;
                    }

                    if (emptyNameSurnameEmail == false) {
                        /**Controllo  email valida*/
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                            try {
                                URL url = new URL(str);
                                AsyncEditProfile utente = new AsyncEditProfile();
                                result = utente.execute(url).get();
                                if (result == false) {
                                    alreadyUsedEmail(ET_email); /**email già presente nel db**/
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                            invalidEmail(ET_email); /**formato email non valido**/
                        }
                    }
                    if (result == true) {
                        BT_pastRentals.setVisibility(View.VISIBLE);
                        R_confirmEdit.setVisibility(View.GONE);
                        R_cancelEdit.setVisibility(View.GONE);

                        ET_name.setEnabled(false);
                        ET_surname.setEnabled(false);
                        ET_email.setEnabled(false);
                        editProfile.setVisibility(View.VISIBLE);

                        /**visualizzazione profilo aggiornato **/
                        viewProfile();
                    }
                }
            });


        /**Al momento del click del bottone cancel vengono annullate tutte le
         * modifiche effettuate in precedenza
         */
        BT_cancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BT_pastRentals.setVisibility(View.VISIBLE);
                R_confirmEdit.setVisibility(View.GONE);
                R_cancelEdit.setVisibility(View.GONE);

                ET_name.setEnabled(false);
                ET_surname.setEnabled(false);
                ET_email.setEnabled(false);
                editProfile.setVisibility(View.VISIBLE);
                viewProfile();
            }
        });


    }

    /**
     * @param ETname
     * formato name non valido
     */
    private void invalidName(EditText ETname) {
        ETname.setHintTextColor(Color.RED);
        ETname.setHint(R.string.not_valid_name);
    }

    /**
     * @param ETsurname
     * formato surname non valido
     */
    private void invalidSurname(EditText ETsurname) {
        ETsurname.setHintTextColor(Color.RED);
        ETsurname.setHint(R.string.not_valid_surname);
    }

    /**
     * @param ETemail
     * formato email non valido
     */
    private void invalidEmail(EditText ETemail) {
        ETemail.setText("");
        ETemail.setHintTextColor(Color.RED);
        ETemail.setHint(R.string.not_valid_email_address);
    }

    /**
     * email scelta è gia in uso
     */
    private void alreadyUsedEmail(EditText ETemail)
    {
        ETemail.setText("");
        ETemail.setHintTextColor(Color.RED);
        ETemail.setHint(R.string.email_already_used);
    }
}
