package com.youssef.barber.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.youssef.barber.R;
import com.youssef.barber.models.Appointment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointments;
    private OnAppointmentActionListener listener;

    public interface OnAppointmentActionListener {
        void onCancelAppointment(Appointment appointment);
        void onViewDetails(Appointment appointment);
    }

    public AppointmentAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
        this.listener = null; // No listener
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);

        // Format the timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        String formattedDate = sdf.format(new Date(appointment.getTimestamp()));

        holder.dateTextView.setText(formattedDate);
        holder.statusTextView.setText(appointment.getStatus());

        holder.cancelButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelAppointment(appointment);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewDetails(appointment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        TextView statusTextView;
        Button cancelButton;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            statusTextView = itemView.findViewById(R.id.textViewStatus);
            cancelButton = itemView.findViewById(R.id.buttonCancel);
        }
    }
}