package it.uniba.di.gruppo17;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import it.uniba.di.gruppo17.asynchttp.AsyncAddReport;
import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.Reporting;

/** @author Pasquale Sansonna, Sgarra Claudia
 *  Fragment per l'invio della segnalazione di problemi al monopattino da parte dell'utente
 */
public class ReportProblemsRentEndedFragment extends Fragment {

    private SharedPreferences prefs;
    private CheckBox brakesCheckBox;
    private CheckBox wheelsCheckBox;
    private CheckBox handlebarsCheckBox;
    private CheckBox acceleratorCheckBox;
    private CheckBox lockCheckBox;
    private CheckBox otherCheckBox;
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
        return inflater.inflate(R.layout.fragment_report_problems_rent_ended, container, false);
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
        reportButton =  getView().findViewById(R.id.buttonConfirmReport);

    }

    @Override
    public void onResume() {
        super.onResume();

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                report = new Reporting(prefs.getInt(Keys.SCOOTER_ID,0), 0, prefs.getInt(Keys.USER_ID, 0), 0, 0, 0, 0, 0, 0 );
                if(brakesCheckBox.isChecked()){
                    report.setBrakes(1);
                }
                if(wheelsCheckBox.isChecked()){
                    report.setWheels(1);
                }
                if(handlebarsCheckBox.isChecked()){
                    report.setHandlebars(1);
                }
                if(acceleratorCheckBox.isChecked()){
                    report.setAccelerator(1);
                }
                if(lockCheckBox.isChecked()){
                    report.setLock(1);
                }
                if(otherCheckBox.isChecked()){
                    report.setOther(1);
                }

                boolean ok = false;
                String server = Keys.SERVER + "add_segnalazione.php?id_utente="+report.getIdUser()+"&id_monopattino="+report.getIdScooter()+"&guasto_freni="+report.isBrakesBroken()+"&guasto_ruote="+report.isWheelsBroken() +"&guasto_manubrio="+report.isHandlebarsBroken()+ "&guasto_acceleratore="+report.isAcceleratorBroken()+ "&guasto_blocco="+report.isLockBroken()+ "&guasto_altro="+report.isOtherBroken();
                try {
                    URL url = new URL(server);
                    AsyncAddReport asyncAddReport = new AsyncAddReport();
                    ok = asyncAddReport.execute(url).get();
                } catch (MalformedURLException | InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                if ( ok )
                    Toast.makeText(getContext(), R.string.reportCompleted_title, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), R.string.reportFailed_text, Toast.LENGTH_SHORT).show();

                getFragmentManager().popBackStack();
            }
        });


    }
}

