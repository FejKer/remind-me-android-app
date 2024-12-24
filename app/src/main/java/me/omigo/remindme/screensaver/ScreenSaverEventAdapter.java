package me.omigo.remindme.screensaver;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import me.omigo.remindme.R;
import me.omigo.remindme.events.Event;

public class ScreenSaverEventAdapter extends RecyclerView.Adapter<ScreenSaverEventAdapter.EventViewHolder> implements EventScreenSaverActivity.BackgroundColorListener {
    private List<Event> events;
    private int textColor;
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

        // Set text color based on time until event
        holder.daysLeftTextView.setTextColor(textColor);
        holder.titleTextView.setTextColor(textColor);
        holder.timeTextView.setTextColor(textColor);

        // Calculate days text
        String daysText;

        if (event.getDate().equals(LocalDate.now())) {
            daysText = "Dziś";
        } else if (event.getDate().minusDays(1).equals(LocalDate.now())) {
            daysText = "Jutro";
        } else if (event.getDate().minusDays(2).equals(LocalDate.now())) {
            daysText = "Pojutrze";
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

    @Override
    public void setColor(int textColor) {
        this.textColor = textColor;
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