package it.uniba.di.gruppo17;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class HomeFragment extends Fragment {


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



}
