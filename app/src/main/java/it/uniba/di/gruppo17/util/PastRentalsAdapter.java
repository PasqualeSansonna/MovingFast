package it.uniba.di.gruppo17.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import it.uniba.di.gruppo17.MapsActivityPastRentals;
import it.uniba.di.gruppo17.R;



public class PastRentalsAdapter extends RecyclerView.Adapter<PastRentalsAdapter.ViewHolder>{

    private List<Rental> rentals;
    private Context context;
    private Rental rental;

    public PastRentalsAdapter(List<Rental> rentals)
    {
        this.rentals = rentals;

    }

    @Override
    public PastRentalsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_rental,parent,false);
        PastRentalsAdapter.ViewHolder viewHolder = new PastRentalsAdapter.ViewHolder(view);
        context = parent.getContext();

      return viewHolder;
    }

    @Override
    public void onBindViewHolder(PastRentalsAdapter.ViewHolder holder, final int position) {

        rental = rentals.get(position);


        if(rental.getDurata() < 60){
            holder.durationTextView.setText(rental.getDurata() +"m");
        }else{
            holder.durationTextView.setText((rental.getDurata()/60) + "h " + rental.getDurata()%60 + "m");
        }

        holder.dataTextView.setText(rental.getData());
        holder.startHourTextView.setText(rental.getOraInizio());
        holder.endHourTextView.setText(rental.getOraFine());
        holder.amountTextView.setText(String.format("%.2f", rental.getImporto()) + "€");


        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivityPastRentals.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("rental", rentals.get(position));
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return rentals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView dataTextView;
        TextView durationTextView;
        TextView startHourTextView;
        TextView endHourTextView;
        TextView amountTextView;
        ImageButton details;
        CardView cardRental;


        public ViewHolder(View itemView)
        {
            super(itemView);

            details = (ImageButton) itemView.findViewById(R.id.buttonDetails);
            dataTextView = (TextView)itemView.findViewById(R.id.dataTextView);
            durationTextView = (TextView)itemView.findViewById(R.id.valueDurationTextView);
            startHourTextView = (TextView)itemView.findViewById(R.id.valueStartHourTextView);
            endHourTextView = (TextView)itemView.findViewById(R.id.valueEndHourTextView);
            amountTextView = (TextView) itemView.findViewById(R.id.valueAmountTextView);
            cardRental = (CardView)itemView.findViewById(R.id.cardRental);
        }

    }

}