package me.omigo.remindme;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventDialogFragment extends DialogFragment implements CustomTimePickerDialog.OnTimeSelectedListener {

    private EditText editTitle, editPlace;
    private TextView dateTextView, timeTextView;
    private SwitchMaterial isImportantSwitch;
    private LocalDate selectedDate;
    private LocalTime selectedTime;
    private Event eventToEdit;
    private boolean isEditing = false;

    private AppDatabase appDatabase;
    private EventDao eventDao;
    private EventDialogListener listener;

    private SwitchMaterial isRecurringSwitch;
    private RadioGroup recurringTypeGroup;
    private View recurringOptionsLayout;
    private View customRecurringLayout;
    private EditText recurringInterval;
    private AutoCompleteTextView recurringUnit;


    @Override
    public void onTimeSelected(LocalTime time) {
        this.selectedTime = time;
    }

    @Override
    public void onTimeCleared() {
        this.selectedTime = null;
    }

    public interface EventDialogListener {
        void onEventSaved(Event event);
    }

    public void setEventToEdit(Event event) {
        this.eventToEdit = event;
        this.isEditing = true;
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
        isImportantSwitch = view.findViewById(R.id.isImportant);


        isRecurringSwitch = view.findViewById(R.id.isRecurring);
        recurringOptionsLayout = view.findViewById(R.id.recurringOptionsLayout);
        recurringTypeGroup = view.findViewById(R.id.recurringTypeGroup);
        customRecurringLayout = view.findViewById(R.id.customRecurringLayout);
        recurringInterval = view.findViewById(R.id.recurringInterval);
        recurringUnit = view.findViewById(R.id.recurringUnit);


        List<String> units = Arrays.stream(TimeUnit.values())
                .map(TimeUnit::getLabel)
                .collect(Collectors.toUnmodifiableList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                units
        );
        recurringUnit.setAdapter(adapter);

        setupRecurringListeners();


        if (isEditing && eventToEdit != null) {
            editTitle.setText(eventToEdit.getTitle());
            editPlace.setText(eventToEdit.getPlace());
            selectedDate = eventToEdit.getDate();
            selectedTime = eventToEdit.getTime();
            dateTextView.setText(selectedDate.toString());
            if (selectedTime != null) {
                timeTextView.setText(selectedTime.toString());
            }
            isImportantSwitch.setChecked(eventToEdit.getPriority() == Priority.IMPORTANT);
            setupRecurringUI(eventToEdit);
        }

        Button datePickerButton = view.findViewById(R.id.datePickerButton);
        datePickerButton.setOnClickListener(v -> showDatePicker());

        Button timePickerButton = view.findViewById(R.id.timePickerButton);
        timePickerButton.setOnClickListener(v -> showTimePicker());

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveEvent());

        return view;
    }

    private void setupRecurringUI(Event eventToEdit) {
        boolean isRecurring = eventToEdit.getRecurring();
        isRecurringSwitch.setChecked(isRecurring);
        recurringOptionsLayout.setVisibility(isRecurring ? View.VISIBLE : View.GONE);

        if (isRecurring) {
            recurringTypeGroup.check(R.id.customRecurring);
            customRecurringLayout.setVisibility(View.VISIBLE);

            recurringInterval.setText(String.valueOf(eventToEdit.getRecurringValue()));
            recurringUnit.setText(eventToEdit.getRecurringTimeUnit().getLabel(), false);
        }
    }


    private void setupRecurringListeners() {
        isRecurringSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recurringOptionsLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        recurringTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.customRecurring) {
                customRecurringLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void saveEvent() {
        String title = editTitle.getText().toString();
        String place = editPlace.getText().toString();

        if (title.isEmpty() || selectedDate == null) {
            Toast.makeText(requireContext(), "Wypełnij wymagany tytuł oraz datę", Toast.LENGTH_LONG).show();
            return;
        }

        if (isRecurringSwitch.isChecked()) {
            String intervalStr = recurringInterval.getText().toString();
            if (intervalStr.isEmpty() || recurringUnit.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Wypełnij interwał i jednostkę czasu", Toast.LENGTH_LONG).show();
                return;
            }
        }


        appDatabase = AppDatabase.getDatabase(requireContext());
        eventDao = appDatabase.eventDao();

        Log.d("time", "time " + selectedTime);

        Event event;
        if (isEditing) {
            event = eventToEdit;
            event.setTitle(title);
            event.setPlace(place);
            event.setDate(selectedDate);
            event.setTime(selectedTime);
            event.setPriority(isImportantSwitch.isChecked() ? Priority.IMPORTANT : Priority.NORMAL);

            event.setRecurring(isRecurringSwitch.isChecked());
            event.setRecurringValue(Integer.parseInt(recurringInterval.getText().toString()));
            event.setRecurringTimeUnit(TimeUnit.fromLabel(recurringUnit.getText().toString()));

            eventDao.update(event);
        } else {
            event = new Event(title, place, selectedDate, selectedTime,
                    isImportantSwitch.isChecked() ? Priority.IMPORTANT : Priority.NORMAL);

            event.setRecurring(isRecurringSwitch.isChecked());
            event.setRecurringValue(Integer.parseInt(recurringInterval.getText().toString()));
            event.setRecurringTimeUnit(TimeUnit.fromLabel(recurringUnit.getText().toString()));

            long insert = eventDao.insert(event);
            event.setId(insert);
        }

        Toast.makeText(requireContext(), "Zapisano", Toast.LENGTH_SHORT).show();

        if (listener != null) {
            listener.onEventSaved(event);
        }

        dismiss();
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
        CustomTimePickerDialog dialog = CustomTimePickerDialog.newInstance(selectedTime, timeTextView, this);
        dialog.show(getChildFragmentManager(), "timePicker");
    }
}
