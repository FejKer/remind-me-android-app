package me.omigo.remindme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Event> events;

    public RecyclerViewAdapter(List<Event> events) {
        this.events = events;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewPlace;
        public TextView textViewDate;
        public TextView textViewTime;
        public TextView textViewIsImportant;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewPlace = itemView.findViewById(R.id.textViewPlace);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewIsImportant = itemView.findViewById(R.id.textViewIsImportant);
        }
    }

    // Inflate item layout and create ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_view, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to each item view
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.textViewTitle.setText(event.getTitle());
        String place = Optional.ofNullable(event.getPlace()).orElse("");
        holder.textViewPlace.setText(place);
        holder.textViewDate.setText(event.getDate().toString());
        String time = Optional.ofNullable(event.getTime()).map(x -> x.toString()).orElse("Całodniowe");
        holder.textViewTime.setText(time);
        holder.textViewIsImportant.setText(Optional.ofNullable(event.getPriority()).orElse(Priority.NORMAL).getLabel());
    }

    public void updateEvents(Event newEvents) {
        Integer index = this.events.size();
        this.events.add(newEvents);
        notifyItemChanged(index);
    }

    public void removeEvents() {
        int size = this.events.size();
        this.events.clear();
        notifyItemRangeRemoved(0, size);
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return events.size();
    }
}
