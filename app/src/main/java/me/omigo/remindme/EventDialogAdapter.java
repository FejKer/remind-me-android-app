package me.omigo.remindme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventDialogAdapter extends RecyclerView.Adapter<EventDialogAdapter.EventViewHolder> {
    private List<Event> events;

    public EventDialogAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_calendar_item, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);

        // Format time
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = event.getTime() != null
                ? event.getTime().format(timeFormatter)
                : "Całodniowe";

        // Set event details
        holder.titleText.setText(event.getTitle());
        holder.placeText.setText(event.getPlace());
        holder.timeText.setText(formattedTime);

        // Set importance with color coding
        holder.importanceText.setText(event.getPriority().getLabel());
        switch (event.getPriority()) {
            case IMPORTANT:
                holder.importanceText.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
                break;
            case NORMAL:
                holder.importanceText.setTextColor(holder.itemView.getContext().getColor(android.R.color.darker_gray));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEvents(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView placeText;
        TextView timeText;
        TextView importanceText;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.eventTitleText);
            placeText = itemView.findViewById(R.id.eventPlaceText);
            timeText = itemView.findViewById(R.id.eventTimeText);
            importanceText = itemView.findViewById(R.id.eventImportanceText);
        }
    }
}
