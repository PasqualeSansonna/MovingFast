package it.uniba.di.gruppo17;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import it.uniba.di.gruppo17.asynchttp.AsyncAddDiscount;
import it.uniba.di.gruppo17.asynchttp.AsyncPastRentals;
import it.uniba.di.gruppo17.util.Keys;
import it.uniba.di.gruppo17.util.Rental;

import static android.content.Context.MODE_PRIVATE;
import static it.uniba.di.gruppo17.util.Keys.USER_ID;


/** @author pasquale, sgarraclaudia
 * Visualizzazione della home contenente le varie funzionalità offerte dall'applicazione
 * (ricerca di scooter, visualizzazione del profilo, visualizzazione del portafoglio)
 * aggiornamento in background della durata dei noleggi utilizzata per il calcolo degli sconti**/
public class HomeFragment extends Fragment {

    ArrayList<Rental> rentals;
    private SharedPreferences preferences;
    int user_ID;

    private ImageView IV_searchScooter;
    private ImageView IV_profile;
    private ImageView IV_wallet;

    ImageView image;
    ImageView image2 = null;
    LinearLayout linearLayout = null;
    Animation logo_anim = null;
    Animation bg_anim;
    Animation fromBottom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        IV_searchScooter = (ImageView) view.findViewById(R.id.IV_SearchScooter);
        IV_profile = (ImageView) view.findViewById(R.id.IV_profile);
        IV_wallet = (ImageView) view.findViewById(R.id.IV_wallet);
        image = (ImageView) view.findViewById(R.id.imageBG);
        image2 = (ImageView) view.findViewById(R.id.imageLogo);
        linearLayout = (LinearLayout) view.findViewById(R.id.LinearLayoutButton);

        fromBottom = AnimationUtils.loadAnimation(this.getActivity(), R.anim.frombottom);
        logo_anim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.logo_scale);
        bg_anim = AnimationUtils.loadAnimation(this.getActivity(), R.anim.bg_home);
        fromBottom.setDuration(1000);
        linearLayout.startAnimation(fromBottom);
        image.animate().translationY(-1650).setDuration(800);
        logo_anim.setDuration(1000);
        image2.startAnimation(logo_anim);



        IV_searchScooter.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment nextFragment = new MapsFragment();
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.commit();

            }
        });

        IV_profile.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment nextFragment = new ProfileFragment();
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.commit();

            }
        });


        IV_wallet.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment nextFragment = new WalletFragment();
                fragmentTransaction.replace(R.id.fragment_container, nextFragment);
                fragmentTransaction.commit();

            }
        });


        return view;
    }


    @Override
    public void onResume() {

        super.onResume();
        Integer ora_totale;
        ora_totale = totalDurationRentals();

        try {
            addDiscount(ora_totale);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    /** calcolo la durata totale di tutti i noleggi per poi calcolare lo sconto corrispondente**/
    private int totalDurationRentals(){

        int durata_totale = 0;
        int ora_totale = 0;

        /**
         * Creo url connessione
         **/
        preferences = getActivity().getSharedPreferences("MovingFastPreferences", MODE_PRIVATE);
        user_ID = Integer.parseInt(preferences.getAll().get(USER_ID).toString());

        String str = Keys.SERVER + "view_past_rentals.php?id=" + user_ID;
        URL url = null;

        try {
            url = new URL(str);
            AsyncPastRentals pastRentals = new AsyncPastRentals();
            rentals = new ArrayList<>();
            rentals = pastRentals.execute(url).get(); //lista dei noleggi

            Log.d("prova", rentals.toString());

            /**Calcolo durata totale noleggi**/
            for (int i =0; i< rentals.size(); i++) {

                durata_totale += rentals.get(i).getDurata();

            }
            ora_totale =  durata_totale / 60;

        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return ora_totale;
    }



    /**in base alla durata totale dei noleggi assegno uno sconto che verrà detratto dall'importo finale per prossimo noleggio dell'utente**/
    private void addDiscount(int ora_totale) throws MalformedURLException {

        Integer discount;


        switch(ora_totale){
            case 1:
                discount = 10;
                break;
            case 3:
                discount = 20;
                break;
            case 5:
                discount = 50;
                break;
            default:
                discount = 0;
        }

        String str = Keys.SERVER + "add_discount.php?id=" + user_ID + "&sconto=" + discount;
        URL url = new URL(str);

        AsyncAddDiscount http = new AsyncAddDiscount();
        http.execute(url);


    }


    }
