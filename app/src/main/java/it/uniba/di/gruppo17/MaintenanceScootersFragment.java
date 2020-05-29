package it.uniba.di.gruppo17;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import it.uniba.di.gruppo17.asynchttp.AsyncGetReportings;
import it.uniba.di.gruppo17.services.LocationService;
import it.uniba.di.gruppo17.util.MaintenanceScootersAdapter;
import it.uniba.di.gruppo17.util.Scooter;

import static it.uniba.di.gruppo17.util.Keys.RAGGIO;
import static it.uniba.di.gruppo17.util.Keys.SERVER;

/**
 * @author Sgarra Claudia, Sansonna Pasquale
 *
 * Fragment che contiene recyclerView con le cardview dei monopattini che hanno bisogno di manutenzione
 */
public class MaintenanceScootersFragment extends Fragment {

    ArrayList<Scooter> maintenanceScooters = null;
    RecyclerView recyclerView;
    MaintenanceScootersAdapter maintenanceScootersAdapter;
    View view;
    AsyncGetReportings getScooters = null;
    ImageView refresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_maintenance_scooters, container, false);



        String serverAddress = SERVER + "get_monopattini_manutenz.php?r="+RAGGIO+"&lat="+ LocationService.realTimeDeviceLocation().getLatitude()+"&long="
                +LocationService.realTimeDeviceLocation().getLongitude();

        try{

            URL urlScooters = new URL (serverAddress);
            getScooters = new AsyncGetReportings();
            maintenanceScooters = new ArrayList<>();
            maintenanceScooters = getScooters.execute(urlScooters).get();
            Thread.sleep(500);

            maintenanceScootersAdapter = new MaintenanceScootersAdapter(maintenanceScooters);

        } catch (MalformedURLException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }



        //Creo cardview con adapter e card
        recyclerView = (RecyclerView) view.findViewById(R.id.maintenanceScootersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(maintenanceScootersAdapter);
        return view;



    }
}
