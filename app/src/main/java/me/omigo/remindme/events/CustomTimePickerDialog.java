package me.omigo.remindme.events;



import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.time.LocalTime;

import me.omigo.remindme.R;

public class CustomTimePickerDialog extends DialogFragment {
    private LocalTime selectedTime;
    private TimePicker timePicker;
    private OnTimeSelectedListener listener;
    private TextView timeTextView;

    public interface OnTimeSelectedListener {
        void onTimeSelected(LocalTime time);
        void onTimeCleared();
    }

    public static CustomTimePickerDialog newInstance(LocalTime initialTime, TextView timeTextView) {
        CustomTimePickerDialog dialog = new CustomTimePickerDialog();
        dialog.selectedTime = initialTime;
        dialog.timeTextView = timeTextView;
        return dialog;
    }

    public static CustomTimePickerDialog newInstance(LocalTime initialTime, TextView timeTextView, OnTimeSelectedListener listener) {
        CustomTimePickerDialog dialog = new CustomTimePickerDialog();
        dialog.selectedTime = initialTime;
        dialog.timeTextView = timeTextView;
        dialog.listener = listener;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_time_picker, null);
        timePicker = view.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        if (selectedTime != null) {
            timePicker.setHour(selectedTime.getHour());
            timePicker.setMinute(selectedTime.getMinute());
        }

        builder.setView(view)
                .setPositiveButton("Ustaw", (dialog, id) -> {
                    selectedTime = LocalTime.of(timePicker.getHour(), timePicker.getMinute());
                    timeTextView.setText(selectedTime.toString());
                    if (listener != null) {
                        listener.onTimeSelected(selectedTime);
                    }
                })
                .setNegativeButton("Anuluj", (dialog, id) -> {
                    // Do nothing - dialog will dismiss automatically
                })
                .setNeutralButton("Wyczyść", (dialog, id) -> {
                    selectedTime = null;
                    timeTextView.setText("Brak godziny - całodniowe");
                    if (listener != null) {
                        listener.onTimeCleared();
                    }
                });

        return builder.create();
    }
}
