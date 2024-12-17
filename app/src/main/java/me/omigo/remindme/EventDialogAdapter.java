package me.omigo.remindme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.format.DateTimeFormatter;
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
        int newEventId = newEvent.getId();
        Optional<Event> optionalEvent = this.events.stream().filter(e -> e.getId() == newEventId).findFirst();
        if (optionalEvent.isPresent()) {
            int index = this.events.indexOf(optionalEvent.get());
            this.events.set(index, newEvent);
            notifyItemChanged(index);
        } else {
            int index = this.events.size();
            this.events.add(newEvent);
            notifyItemInserted(index);  // Changed from notifyItemChanged
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, placeText, dateText, timeText, importanceText;
        ImageButton editButton;

        ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.eventTitleText);
            placeText = view.findViewById(R.id.eventPlaceText);
            dateText = view.findViewById(R.id.eventDateText);
            timeText = view.findViewById(R.id.eventTimeText);
            importanceText = view.findViewById(R.id.eventImportanceText);
            editButton = view.findViewById(R.id.editButton);
        }
    }
}