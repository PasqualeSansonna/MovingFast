package it.uniba.di.gruppo17;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import it.uniba.di.gruppo17.asynchttp.AsyncGetUnloadedScooters;
import it.uniba.di.gruppo17.services.LocationService;
import it.uniba.di.gruppo17.util.Scooter;
import it.uniba.di.gruppo17.util.UnloadedScootersAdapter;

import static it.uniba.di.gruppo17.util.Keys.RAGGIO;
import static it.uniba.di.gruppo17.util.Keys.SERVER;


/**
 * @author Andrea Montemurro
 *
 * Fragment che contiene recyclerView con le cardview dei monopattini da ricaricare per il manutentore
 */
public class UnloadedScootersFragment extends Fragment {

    ArrayList<Scooter> unloadedScooters = null;
    RecyclerView recyclerView;
    UnloadedScootersAdapter unloadedScootersAdapter;
    View view;

    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_unloaded_scooters, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                getUnloadedScooters();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });

        getUnloadedScooters();


        return view;
    }

    public void getUnloadedScooters(){
        Scooter.clearNearScooters();

        String serverAddress = SERVER + "get_monopattini.php?r="+RAGGIO+"&lat="+ LocationService.realTimeDeviceLocation().getLatitude()+"&long="
                +LocationService.realTimeDeviceLocation().getLongitude();
        AsyncGetUnloadedScooters getScooters = null;
        try{
            URL urlScooters = new URL (serverAddress);
            getScooters = new AsyncGetUnloadedScooters();
            getScooters.execute(urlScooters);
            Thread.sleep(500);
        } catch (MalformedURLException | InterruptedException e) {
            e.printStackTrace();
        }
        unloadedScooters = new ArrayList<>();

        for (Scooter s : Scooter.nearScooters)
            unloadedScooters.add(s);

        UnloadedScootersAdapter unloadedScootersAdapter = new UnloadedScootersAdapter(unloadedScooters);

        //Creo cardview con adapter e card
        recyclerView = (RecyclerView) view.findViewById(R.id.unloadedScootersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(unloadedScootersAdapter);
    }
}
