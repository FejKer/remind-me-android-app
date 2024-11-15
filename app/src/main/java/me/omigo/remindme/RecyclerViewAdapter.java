package me.omigo.remindme;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Event> events;

    // Constructor for the adapter
    public RecyclerViewAdapter(List<Event> events) {
        this.events = events;
    }

    // ViewHolder class to hold item views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewPlace;
        public TextView textViewDate;
        public TextView textViewTime;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewPlace = itemView.findViewById(R.id.textViewPlace);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
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
        holder.textViewPlace.setText(event.getPlace());
        holder.textViewDate.setText(event.getDate().toString());
        holder.textViewTime.setText(event.getTime().toString());
    }

    public void updateEvents(Event newEvents) {
        Integer index = this.events.size();
        this.events.add(newEvents);
        notifyItemChanged(index);
    }

    // Return the total count of items
    @Override
    public int getItemCount() {
        return events.size();
    }
}
