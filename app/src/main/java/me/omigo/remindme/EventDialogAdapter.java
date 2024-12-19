package me.omigo.remindme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EventDialogAdapter extends RecyclerView.Adapter<EventDialogAdapter.ViewHolder> {
    private List<Event> events;
    private OnEventEditListener editListener;

    public interface OnEventEditListener {
        void onEventEdit(Event event);
    }

    public EventDialogAdapter(List<Event> events) {
        this.events = events;
    }

    public void setOnEventEditListener(OnEventEditListener listener) {
        this.editListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_calendar_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = events.get(position);

        holder.titleText.setText(event.getTitle());
        holder.placeText.setText(event.getPlace());
        holder.dateText.setText(event.getDate().toString());
        if (event.getTime() != null) {
            holder.timeText.setText(event.getTime().toString());
        } else {
            holder.timeText.setText("Całodniowe");
        }
        holder.importanceText.setText(event.getPriority() == Priority.IMPORTANT ?
                "Ważne" : "Normalne");
        holder.importanceText.setTextColor(event.getPriority() == Priority.IMPORTANT ?
                holder.itemView.getContext().getColor(android.R.color.holo_red_dark) :
                holder.itemView.getContext().getColor(android.R.color.darker_gray));

        if (event.getRecurring()) {
            holder.recurringImageView.setVisibility(View.VISIBLE);
        }

        holder.editButton.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEventEdit(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEvents(Event newEvent) {
        long newEventId = newEvent.getId();
        Optional<Event> optionalEvent = this.events.stream()
                .filter(e -> e.getId() == newEventId)
                .findFirst();

        if (optionalEvent.isPresent()) {
            // Update existing event
            int oldIndex = this.events.indexOf(optionalEvent.get());
            this.events.remove(oldIndex);
            notifyItemRemoved(oldIndex);
        }

        // Find correct position to insert based on sorting criteria
        int insertIndex = findSortedPosition(newEvent);
        this.events.add(insertIndex, newEvent);
        notifyItemInserted(insertIndex);
    }

    private int findSortedPosition(Event newEvent) {
        int searchResult = Collections.binarySearch(this.events, newEvent, (event1, event2) -> {
            // First compare by date
            int dateComparison = event1.getDate().compareTo(event2.getDate());
            if (dateComparison != 0) {
                return dateComparison;
            }

            // If dates are equal, compare by time
            LocalTime time1 = event1.getTime();
            LocalTime time2 = event2.getTime();

            // Handle null times (null comes first)
            if (time1 == null && time2 == null) return 0;
            if (time1 == null) return -1;
            if (time2 == null) return 1;

            return time1.compareTo(time2);
        });
        return searchResult < 0 ? Math.max(0, -(searchResult + 1)) : searchResult;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, placeText, dateText, timeText, importanceText;
        ImageButton editButton;
        ImageView recurringImageView;

        ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.eventTitleText);
            placeText = view.findViewById(R.id.eventPlaceText);
            dateText = view.findViewById(R.id.eventDateText);
            timeText = view.findViewById(R.id.eventTimeText);
            importanceText = view.findViewById(R.id.eventImportanceText);
            editButton = view.findViewById(R.id.editButton);
            recurringImageView = view.findViewById(R.id.imageViewRecurring);
        }
    }
}