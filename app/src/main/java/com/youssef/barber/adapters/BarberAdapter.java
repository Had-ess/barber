package com.youssef.barber.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.youssef.barber.R;
import com.youssef.barber.models.Barber;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BarberAdapter extends RecyclerView.Adapter<BarberAdapter.BarberViewHolder> {

    private Context context;
    private List<Barber> barberList;
    private OnBarberClickListener listener;

    public interface OnBarberClickListener {
        void onBarberClick(Barber barber);
    }

    public BarberAdapter(Context context, List<Barber> barberList, OnBarberClickListener listener) {
        this.context = context;
        this.barberList = barberList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BarberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_barber, parent, false);
        return new BarberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarberViewHolder holder, int position) {
        Barber barber = barberList.get(position);

        holder.tvBarberName.setText(barber.getName());
        holder.tvBarberSpecialty.setText(barber.getSpecialty());
        holder.ratingBar.setRating(barber.getRating());

        if (barber.getPhotoUrl() != null && !barber.getPhotoUrl().isEmpty()) {
            Picasso.get()
                    .load(barber.getPhotoUrl())
                    .placeholder(R.drawable.ic_barber_placeholder)
                    .error(R.drawable.ic_barber_placeholder)
                    .into(holder.ivBarberPhoto);
        } else {
            holder.ivBarberPhoto.setImageResource(R.drawable.ic_barber_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBarberClick(barber);
            }
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public void updateData(List<Barber> newBarberList) {
        barberList = newBarberList;
        notifyDataSetChanged();
    }

    static class BarberViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBarberPhoto;
        TextView tvBarberName;
        TextView tvBarberSpecialty;
        RatingBar ratingBar;

        public BarberViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBarberPhoto = itemView.findViewById(R.id.ivBarberPhoto);
            tvBarberName = itemView.findViewById(R.id.tvBarberName);
            tvBarberSpecialty = itemView.findViewById(R.id.tvBarberSpecialty);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}