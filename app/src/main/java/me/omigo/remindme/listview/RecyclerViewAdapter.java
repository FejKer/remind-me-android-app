package me.omigo.remindme.listview;

import android.content.Context;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import me.omigo.remindme.AppDatabase;
import me.omigo.remindme.events.Event;
import me.omigo.remindme.events.EventDao;
import me.omigo.remindme.events.Priority;
import me.omigo.remindme.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Event> events;
    private OnEventEditListener editListener;
    private OnEventDeleteListener deleteListener;
    private EventDao eventDao;

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

    public interface OnEventDeleteListener {
        void onEventDelete(Event event);
    }

    public RecyclerViewAdapter(List<Event> events, Context context) {
        this.events = events;
        this.eventDao = AppDatabase.getDatabase(context).eventDao();
    }

    public void setOnEventEditListener(OnEventEditListener listener) {
        this.editListener = listener;
    }

    public void setOnEventDeleteListener(OnEventDeleteListener listener) {
        this.deleteListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewPlace;
        public TextView textViewDate;
        public TextView textViewTime;
        public TextView textViewIsImportant;
        public TextView textViewRecurring;
        public ImageButton editButton;
        public ImageButton deleteButton;
        public ImageView imageViewRecurring;
        public ImageView imageViewIsHidden;

        public MaterialCardView materialCardView;


        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewPlace = itemView.findViewById(R.id.textViewPlace);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewIsImportant = itemView.findViewById(R.id.textViewIsImportant);
            editButton = itemView.findViewById(R.id.editButton);
            imageViewRecurring = itemView.findViewById(R.id.imageViewRecurring);
            materialCardView = itemView.findViewById(R.id.materialCardView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            imageViewIsHidden = itemView.findViewById(R.id.imageViewScreenSaverHidden);
            textViewRecurring = itemView.findViewById(R.id.textViewRecurring);
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
        if (!place.isEmpty()) {
            place = "Miejsce: " + place;
        }
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
                // If this is a recurring instance, edit the parent event
                if (event.getParentEventId() != null && event.getParentEventId() != 0L) {
                    // Find the parent event and edit it instead
                    Event parentEvent = findParentEvent(event.getParentEventId());
                    if (parentEvent != null) {
                        editListener.onEventEdit(parentEvent);
                    }
                } else {
                    editListener.onEventEdit(event);
                }
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            Log.d("recurring", "outside delete");
            if (deleteListener != null) {
                Log.d("recurring", "inside delete");
                Context context = v.getContext();
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                View dialogView = LayoutInflater.from(context).inflate(R.layout.delete_confirmation_dialog, null);

                TextView messageText = dialogView.findViewById(R.id.messageText);
                Button positiveButton = dialogView.findViewById(R.id.positiveButton);
                Button negativeButton = dialogView.findViewById(R.id.negativeButton);

                messageText.setText("Czy na pewno usunąć to wydarzenie?");
                positiveButton.setEnabled(false);
                positiveButton.setText("TAK (5)");

                AlertDialog dialog = builder
                        .setView(dialogView)
                        .setCancelable(true)
                        .create();

                // Set up countdown timer
                new CountDownTimer(5000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        int secondsLeft = (int) (millisUntilFinished / 1000);
                        positiveButton.setText("TAK (" + secondsLeft + ")");
                    }

                    public void onFinish() {
                        positiveButton.setEnabled(true);
                        positiveButton.setText("TAK");
                    }
                }.start();

                positiveButton.setOnClickListener(dialogButton -> {
                    deleteListener.onEventDelete(event);
                    dialog.dismiss();
                });

                negativeButton.setOnClickListener(dialogButton -> dialog.dismiss());

                dialog.show();
            }
        });

        holder.textViewRecurring.setVisibility(View.GONE);

        if (event.getRecurring() || (event.getParentEventId() != null && !event.getParentEventId().equals(0L))) {
            holder.imageViewRecurring.setVisibility(View.VISIBLE);
            holder.textViewRecurring.setVisibility(View.VISIBLE);
            holder.textViewRecurring.setText(String.format("Powtarza się co %d %s", event.getRecurringValue(), event.getRecurringTimeUnit().getLabel()));
        }

        holder.editButton.setVisibility(View.VISIBLE);

        holder.imageViewIsHidden.setVisibility(event.getHiddenFromScreenSaver() ? View.VISIBLE : View.GONE);


        if (event.getParentEventId() != null && !event.getParentEventId().equals(0L)) {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        }

        holder.itemView.setAlpha(
                event.getPriority() == Priority.IMPORTANT ? 1.0f : 0.85f
        );

        if (event.getPriority() == Priority.IMPORTANT) {
            Context context = holder.itemView.getContext();
            Resources resources = context.getResources();

            // Use setStrokeWidth and setStrokeColor methods instead of property access
            holder.materialCardView.setStrokeWidth(resources.getDimensionPixelSize(R.dimen.stroke_width));
            holder.materialCardView.setStrokeColor(ContextCompat.getColor(context, R.color.red));
        } else {
            // Reset the stroke for non-important events
            holder.materialCardView.setStrokeWidth(0);
            // Or set to default color if you prefer
            // holder.materialCardView.setStrokeColor(Color.TRANSPARENT);
        }
    }

    private Event findParentEvent(Long parentEventId) {
        return eventDao.findById(parentEventId);
    }

    public void updateEvents(Event newEvent) {
        long newEventId = newEvent.getId();
        Optional<Event> optionalEvent = this.events.stream()
                .filter(e -> e.getId() == newEventId)
                .filter(e -> e.getParentEventId() == null || e.getParentEventId().equals(0L)) //klony rekurencyjne
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