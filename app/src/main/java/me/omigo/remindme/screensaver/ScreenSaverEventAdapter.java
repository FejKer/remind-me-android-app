package me.omigo.remindme.screensaver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import me.omigo.remindme.R;
import me.omigo.remindme.events.Event;

public class ScreenSaverEventAdapter extends RecyclerView.Adapter<ScreenSaverEventAdapter.EventViewHolder> {
    private List<Event> events;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ScreenSaverEventAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.screen_saver_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);
        holder.titleTextView.setText(event.getTitle());

        LocalDateTime eventDateTime = LocalDateTime.of(
                event.getDate(),
                event.getTime() != null ? event.getTime() : LocalDateTime.now().toLocalTime()
        );

        if (event.getTime() != null) {
            holder.timeTextView.setText(DATE_TIME_FORMATTER.format(eventDateTime));
        } else {
            holder.timeTextView.setText(DATE_FORMATTER.format(event.getDate()) + " całodniowe");
        }

        long daysUntil = ChronoUnit.DAYS.between(LocalDateTime.now(), eventDateTime);
        String daysText;
        if (daysUntil == 0) {
            daysText = "Dziś";
        } else if (daysUntil == 1) {
            daysText = "Jutro";
        } else {
            daysText = "Za " + daysUntil + " dni";
        }
        holder.daysLeftTextView.setText(daysText);
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
        TextView titleTextView;
        TextView timeTextView;
        TextView daysLeftTextView;

        EventViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.eventTitleTextView);
            timeTextView = itemView.findViewById(R.id.eventTimeTextView);
            daysLeftTextView = itemView.findViewById(R.id.eventDaysLeftTextView);
        }
    }
}