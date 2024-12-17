package me.omigo.remindme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewPlace = itemView.findViewById(R.id.textViewPlace);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewIsImportant = itemView.findViewById(R.id.textViewIsImportant);
            editButton = itemView.findViewById(R.id.editButton);
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
                .map(x -> x.toString())
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

        holder.itemView.setAlpha(
                event.getPriority() == Priority.IMPORTANT ? 1.0f : 0.85f
        );
    }

    public void updateEvents(Event newEvents) {
        Integer index = this.events.size();
        this.events.add(newEvents);
        notifyItemInserted(index);  // Changed from notifyItemChanged
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