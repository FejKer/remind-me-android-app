package me.omigo.remindme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventDialogFragment extends DialogFragment {

    private EditText editTitle, editPlace;
    private TextView dateTextView, timeTextView;
    private SwitchMaterial isImportantSwitch;
    private LocalDate selectedDate;
    private LocalTime selectedTime;

    private AppDatabase appDatabase;
    private EventDao eventDao;

    private EventDialogListener listener;

    public interface EventDialogListener {
        void onEventSaved(Event event);
    }

    public void setEventDialogListener(EventDialogListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_dialog, container, false);

        editTitle = view.findViewById(R.id.editTitle);
        editPlace = view.findViewById(R.id.editPlace);
        dateTextView = view.findViewById(R.id.dateTextView);
        timeTextView = view.findViewById(R.id.timeTextView);
        isImportantSwitch = ((SwitchMaterial) view.findViewById(R.id.isImportant));

        Button datePickerButton = view.findViewById(R.id.datePickerButton);
        datePickerButton.setOnClickListener(v -> showDatePicker());

        Button timePickerButton = view.findViewById(R.id.timePickerButton);
        timePickerButton.setOnClickListener(v -> showTimePicker());

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveEvent());

        return view;
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    dateTextView.setText(selectedDate.toString());
                },
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue() - 1,
                LocalDate.now().getDayOfMonth()
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedTime = LocalTime.of(hourOfDay, minute);
                    timeTextView.setText(selectedTime.toString());
                },
                LocalTime.now().getHour(),
                LocalTime.now().getMinute(),
                true
        );
        timePickerDialog.show();
    }

    private void saveEvent() {
        String title = editTitle.getText().toString();
        String place = editPlace.getText().toString();

        if (title.isEmpty() || selectedDate == null) {
            Toast.makeText(requireContext(), "Wypełnij wymagany tytuł oraz datę", Toast.LENGTH_LONG).show();
            return;
        }

        Event event = new Event(title, place, selectedDate, selectedTime, isImportantSwitch.isChecked() ? Priority.IMPORTANT : Priority.NORMAL);

        appDatabase = AppDatabase.getDatabase(requireContext());
        eventDao = appDatabase.eventDao();
        eventDao.insert(event);

        Toast.makeText(requireContext(), "Zapisano", Toast.LENGTH_SHORT).show();

        if (listener != null) {
            listener.onEventSaved(event); // Notify listener about the new event
        }

        dismiss();
    }
}
