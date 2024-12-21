package me.omigo.remindme.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import me.omigo.remindme.R;
import me.omigo.remindme.events.Event;
import me.omigo.remindme.events.Priority;

public class EventDialogAdapter extends RecyclerView.Adapter<EventDialogAdapter.ViewHolder> {
    private List<Event> events;
    private OnEventEditListener editListener;

    public void deleteSlaveEvents(long id) {
        for (var event : new ArrayList<>(events)) {
            if (event.getParentEventId().equals(id)) {
                int index = events.indexOf(event);
                notifyItemRemoved(index);
                events.remove(event);
            }
        }
    }

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


        holder.recurringImageView.setVisibility(View.GONE);
        holder.textViewRecurring.setVisibility(View.GONE);


        if (event.getParentEventId() != null && !event.getParentEventId().equals(0L)) {
            holder.editButton.setVisibility(View.GONE);
            holder.recurringImageView.setVisibility(View.VISIBLE);
            holder.textViewRecurring.setVisibility(View.VISIBLE);
            holder.textViewRecurring.setText(String.format("Powtarza się co %d %s", event.getRecurringValue(), event.getRecurringTimeUnit().getLabel()));
        }

        holder.editButton.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEventEdit(event);
            }
        });

        holder.hiddenFromScreenSaverImageView.setVisibility(event.getHiddenFromScreenSaver() ? View.VISIBLE: View.GONE);

        if (event.getPriority() == Priority.IMPORTANT) {
            Context context = holder.itemView.getContext();
            Resources resources = context.getResources();

            holder.materialCardView.setStrokeWidth(resources.getDimensionPixelSize(R.dimen.stroke_width));
            holder.materialCardView.setStrokeColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.materialCardView.setStrokeWidth(0);

        }
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
        TextView titleText, placeText, dateText, timeText, importanceText, textViewRecurring;
        ImageButton editButton;
        ImageView recurringImageView;
        ImageView hiddenFromScreenSaverImageView;
        MaterialCardView materialCardView;

        ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.eventTitleText);
            placeText = view.findViewById(R.id.eventPlaceText);
            dateText = view.findViewById(R.id.eventDateText);
            timeText = view.findViewById(R.id.eventTimeText);
            importanceText = view.findViewById(R.id.eventImportanceText);
            editButton = view.findViewById(R.id.editButton);
            recurringImageView = view.findViewById(R.id.imageViewRecurring);
            materialCardView = view.findViewById(R.id.materialCardView);
            hiddenFromScreenSaverImageView = view.findViewById(R.id.imageViewScreenSaverHidden);
            textViewRecurring = view.findViewById(R.id.textViewRecurring);
        }
    }
}