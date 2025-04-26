package com.youssef.barber.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.youssef.barber.R;
import com.youssef.barber.interfaces.OnSlotSelectedListener;
import com.youssef.barber.models.TimeSlot;
import java.util.List;

public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    private List<TimeSlot> slots;
    private OnSlotSelectedListener listener;

    public SlotAdapter(List<TimeSlot> slots, OnSlotSelectedListener listener) {
        this.slots = slots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        TimeSlot slot = slots.get(position);
        holder.timeTextView.setText(slot.getTime());
        holder.itemView.setOnClickListener(v -> listener.onSlotSelected(slot));
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;

        SlotViewHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.textViewTime);
        }
    }
}