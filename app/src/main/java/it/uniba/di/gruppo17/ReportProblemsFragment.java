package it.uniba.di.gruppo17;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.net.MalformedURLException;
import java.net.URL;

import it.uniba.di.gruppo17.asynchttp.AsyncAddReport;
import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.Reporting;

/** @author Pasquale Sansonna, Sgarra Claudia
 *  Fragment per l'invio della segnalazione di problemi al monopattino da parte dell'utente
 */
public class ReportProblemsFragment extends Fragment {

    private SharedPreferences prefs;
    private CheckBox brakesCheckBox;
    private CheckBox wheelsCheckBox;
    private CheckBox handlebarsCheckBox;
    private CheckBox acceleratorCheckBox;
    private CheckBox lockCheckBox;
    private CheckBox otherCheckBox;
    private EditText descriptionEditText;
    private Button reportButton;
    private Reporting report;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = this.getActivity().getSharedPreferences(Keys.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        container.removeAllViews();
        return inflater.inflate(R.layout.fragment_report_problems, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        brakesCheckBox = getView().findViewById(R.id.brakesCheckBox);
        wheelsCheckBox = getView().findViewById(R.id.wheelsCheckBox);
        handlebarsCheckBox = getView().findViewById(R.id.handlebarCheckBox);
        acceleratorCheckBox = getView().findViewById(R.id.acceleratorCheckBox);
        lockCheckBox = getView().findViewById(R.id.lockCheckBox);
        otherCheckBox = getView().findViewById(R.id.otherCheckBox);
        descriptionEditText = getView().findViewById(R.id.descriptionEditText);
        reportButton = getView().findViewById(R.id.reportButton);
    }

    @Override
    public void onResume() {
        super.onResume();

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String description;

                report = new Reporting(prefs.getInt(Keys.SCOOTER_ID,0), 0, prefs.getInt(Keys.USER_ID, 0), false, false, false, false, false, false, null );
                if(brakesCheckBox.isChecked()){
                    report.setBrakes(true);
                }
                if(wheelsCheckBox.isChecked()){
                    report.setWheels(true);
                }
                if(handlebarsCheckBox.isChecked()){
                    report.setHandlebars(true);
                }
                if(acceleratorCheckBox.isChecked()){
                    report.setAccelerator(true);
                }
                if(lockCheckBox.isChecked()){
                    report.setLock(true);
                }
                if(otherCheckBox.isChecked()){
                    report.setOther(true);
                }

                description = descriptionEditText.getText().toString();

                if(!description.isEmpty()){
                    report.setDescription(description);
                }

                String server = Keys.SERVER + "add_segnalazione.php?id_utente="+report.getIdUser()+"&id_monopattino="+report.getIdScooter()+"&guasto_freni="+report.isBrakesBroken()+"&guasto_ruote="+report.isWheelsBroken() +"&guasto_manubrio="+report.isHandlebarsBroken()+ "&guasto_acceleratore="+report.isAcceleratorBroken()+ "&guasto_blocco="+report.isLockBroken()+ "&guasto_altro="+report.isOtherBroken()+ "&descrizione="+report.getDescription();
                try {
                    URL url = new URL(server);
                    AsyncAddReport asyncAddReport = new AsyncAddReport();
                    asyncAddReport.execute(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment nextFragment = new ResultCloseRentFragment();
                fragmentTransaction.replace(R.id.fragment_container_maint, nextFragment);
                fragmentTransaction.commit();

            }
        });
    }
}

