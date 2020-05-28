package it.uniba.di.gruppo17.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import it.uniba.di.gruppo17.R;
import it.uniba.di.gruppo17.asynchttp.AsyncEditProfile;

/**
 * @author Andrea Montemurro
 * Adapter recycler view di unloadedscooters
 */
public class UnloadedScootersAdapter extends RecyclerView.Adapter<UnloadedScootersAdapter.ViewHolder> {

    private List<Scooter> unloadedScooters;
    private Context context;
    private Scooter scooter;

    public UnloadedScootersAdapter (List<Scooter> scooters)
    {
        this.unloadedScooters = scooters;
    }

    @Override
    public UnloadedScootersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_unloaded_scooter,parent,false);
        UnloadedScootersAdapter.ViewHolder viewHolder = new UnloadedScootersAdapter.ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UnloadedScootersAdapter.ViewHolder holder, final int position) {
        scooter = unloadedScooters.get(position);

        holder.battery.setText(scooter.getBatteryLevel()+ " %");
        holder.scooterId.setText("ID "+ scooter.getIdScooter());
        //Setto l'indirizzo nella textview
        final LatLng mLatLng = new LatLng(Double.parseDouble(scooter.getLatitude()), Double.parseDouble(scooter.getLongitude()));
        Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = mGeocoder.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
            holder.address.setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Ricarica lo scooter della card*/
        holder.recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = Keys.SERVER + "update_carica_monopattino.php?id=" + unloadedScooters.get(position).getIdScooter();

                try {
                    URL url = new URL(str);
                    //NB: Async Edit Profile va bene, non serve creare un altro async task uguale per fare le stesse cose
                    AsyncEditProfile utente = new AsyncEditProfile();
                    boolean result = utente.execute(url).get();
                    if (result) {
                        Toast.makeText(context, R.string.charged_scooter, Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(context, R.string.not_charged_scooter, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return unloadedScooters.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView address;
        TextView battery;
        TextView scooterId;
        ImageButton recharge;
        CardView cardUnloadedScooter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            address = (TextView) itemView.findViewById(R.id.address_card_unl_textview);
            battery = (TextView) itemView.findViewById(R.id.battery_card_unl_textview);
            scooterId = (TextView) itemView.findViewById(R.id.id_card_unl_textview);
            recharge = (ImageButton) itemView.findViewById(R.id.imageButton_recharge_unl);
            cardUnloadedScooter = (CardView) itemView.findViewById(R.id.cardUnloadedScooter);
        }
    }
}
