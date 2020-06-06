package it.uniba.di.gruppo17;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;

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
    SwipeRefreshLayout swipeRefreshLayout;
    Animation anim;
    MaterialCardView titleCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_maintenance_scooters, container, false);

        titleCard = view.findViewById(R.id.titleMaintenanceScootersCard);
        anim = AnimationUtils.loadAnimation(getContext(), R.anim.translate_horizontal);
        anim.setDuration(700);
        titleCard.startAnimation(anim);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getScooters();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        getScooters();

        return view;

    }

    public void getScooters(){

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
    }
}
