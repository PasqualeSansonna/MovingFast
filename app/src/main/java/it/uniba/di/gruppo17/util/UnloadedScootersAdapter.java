package it.uniba.di.gruppo17.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.di.gruppo17.R;

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
    public void onBindViewHolder(@NonNull UnloadedScootersAdapter.ViewHolder holder, int position) {
        scooter = unloadedScooters.get(position);

        holder.battery.setText(scooter.getBatteryLevel()+ "%");
        holder.scooterId.setText("ID"+ scooter.getIdScooter());

        /*Ricarica lo scooter della card*/
        holder.recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
