package me.omigo.remindme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Event> events;
    private OnEventEditListener editListener;

    public interface OnEventEditListener {
        void onEventEdit(Event event);
    }

    public RecyclerViewAdapter(List<Event> events) {
        this.events = events;
    }

    public void setOnEventEditListener(OnEventEditListener listener) {
        this.editListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewPlace;
        public TextView textViewDate;
        public TextView textViewTime;
        public TextView textViewIsImportant;
        public ImageButton editButton;
        public ImageView imageViewRecurring;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewPlace = itemView.findViewById(R.id.textViewPlace);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewIsImportant = itemView.findViewById(R.id.textViewIsImportant);
            editButton = itemView.findViewById(R.id.editButton);
            imageViewRecurring = itemView.findViewById(R.id.imageViewRecurring);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.textViewTitle.setText(event.getTitle());
        String place = Optional.ofNullable(event.getPlace()).orElse("");
        holder.textViewPlace.setText(place);
        holder.textViewDate.setText(event.getDate().toString());
        String time = Optional.ofNullable(event.getTime())
                .map(LocalTime::toString)
                .orElse("Całodniowe");
        holder.textViewTime.setText(time);
        holder.textViewIsImportant.setText(
                Optional.ofNullable(event.getPriority())
                        .orElse(Priority.NORMAL)
                        .getLabel()
        );

        holder.editButton.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEventEdit(event);
            }
        });

        if (event.getRecurrencePattern() != null) {
            holder.imageViewRecurring.setVisibility(View.VISIBLE);
        }

        holder.itemView.setAlpha(
                event.getPriority() == Priority.IMPORTANT ? 1.0f : 0.85f
        );
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

    public void removeEvents() {
        int size = this.events.size();
        this.events.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}