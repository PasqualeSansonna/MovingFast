package it.uniba.di.gruppo17.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import it.uniba.di.gruppo17.R;
import it.uniba.di.gruppo17.asynchttp.AsyncDeleteReporting;

/**
 * @author Sgarra Claudia, Sansonna Pasquale
 * Adapter recycler view di Maintenance
 */
public class MaintenanceScootersAdapter extends RecyclerView.Adapter<MaintenanceScootersAdapter.ViewHolder> {

    private List<Scooter> maintenanceScooters;
    private Context context;
    private Scooter scooter;

    public MaintenanceScootersAdapter (List<Scooter> scooters)
    {
        this.maintenanceScooters = scooters;

    }

    @Override
    public MaintenanceScootersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_maintenance_scooter,parent,false);
        MaintenanceScootersAdapter.ViewHolder viewHolder = new MaintenanceScootersAdapter.ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MaintenanceScootersAdapter.ViewHolder holder, final int position) {

        scooter = maintenanceScooters.get(position);

        /*Setto l'indirizzo del monopattino nella textview*/
        final LatLng mLatLng = new LatLng(Double.parseDouble(scooter.getLatitude()), Double.parseDouble(scooter.getLongitude()));
        Geocoder mGeocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = mGeocoder.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
            holder.address.setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }



        /* se il monopattino ha un guasto ai freni spunta la relativa checkbox*/
        if(scooter.reportingMaintenance.isBrakesBroken() == 1){
            holder.brakes.setChecked(true);
        }
        /* se il monopattino ha un guasto alle ruote spunta la relativa checkbox*/
        if(scooter.reportingMaintenance.isWheelsBroken() == 1){
            holder.wheels.setChecked(true);
        }
        /* se il monopattino ha un guasto al manubrio spunta la relativa checkbox*/
        if(scooter.reportingMaintenance.isHandlebarsBroken() == 1){
            holder.handlebars.setChecked(true);
        }
        /* se il monopattino ha un guasto all'acceleratore spunta la relativa checkbox*/
        if(scooter.reportingMaintenance.isAcceleratorBroken() == 1){
            holder.accelerator.setChecked(true);
        }
        /* se il monopattino ha un guasto al blocco spunta la relativa checkbox*/
        if(scooter.reportingMaintenance.isLockBroken() == 1){
            holder.lock.setChecked(true);
        }
        /* se il monopattino ha altre tipologie di guasti spunta la relativa checkbox*/
        if(scooter.reportingMaintenance.isOtherBroken() == 1){
            holder.other.setChecked(true);
        }




        /*Conferma l'avvenuta manutenzione*/
        holder.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = Keys.SERVER + "delete_segnalazione.php?id=" + maintenanceScooters.get(position).reportingMaintenance.getIdReporting();

                /*quando termina la manutenzione il manutentore clicca su conferma e la segnalazione relativa al guasto risolto viene cancellata dal db*/
                try {
                    URL url = new URL(str);

                    AsyncDeleteReporting deleteReporting = new AsyncDeleteReporting();
                    boolean result = deleteReporting.execute(url).get();

                    if (result) {
                        Toast.makeText(context, R.string.maintened_scooter, Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        Toast.makeText(context, R.string.already_maintened_scooter, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return maintenanceScooters.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView address;
        TextView idScooter;
        ImageButton confirmButton;
        CardView cardMaintenanceScooter;
        CheckBox brakes;
        CheckBox wheels;
        CheckBox handlebars;
        CheckBox accelerator;
        CheckBox lock;
        CheckBox other;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            address = (TextView) itemView.findViewById(R.id.address_card_maintenance_textview);
            idScooter = (TextView) itemView.findViewById(R.id.idScooterTextView);
            confirmButton = (ImageButton) itemView.findViewById(R.id.imageButton_confirm);
            cardMaintenanceScooter = (CardView) itemView.findViewById(R.id.cardMaintenanceScooter);
            brakes = (CheckBox) itemView.findViewById(R.id.title_brakes);
            brakes.setEnabled(false);
            wheels = (CheckBox) itemView.findViewById(R.id.title_wheels);
            wheels.setEnabled(false);
            handlebars = (CheckBox) itemView.findViewById(R.id.title_handlebars);
            handlebars.setEnabled(false);
            accelerator = (CheckBox) itemView.findViewById(R.id.title_accelerator);
            accelerator.setEnabled(false);
            lock = (CheckBox) itemView.findViewById(R.id.title_lock);
            lock.setEnabled(false);
            other = (CheckBox) itemView.findViewById(R.id.title_other);
            other.setEnabled(false);
        }
    }
}
