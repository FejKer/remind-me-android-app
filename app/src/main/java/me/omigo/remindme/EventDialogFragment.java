package me.omigo.remindme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.time.DayOfWeek;
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
    private SwitchMaterial isRecurringSwitch;
    private RadioGroup recurringTypeGroup;
    private View recurringOptionsLayout;
    private View weekDaysLayout;
    private View customRecurringLayout;
    private EditText recurringInterval;
    private AutoCompleteTextView recurringUnit;
    private CheckBox[] weekDayCheckboxes;
    private RecurrencePattern recurringPattern;


    private AppDatabase appDatabase;
    private EventDao eventDao;
    private EventDialogListener listener;

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
        weekDaysLayout = view.findViewById(R.id.weekDaysLayout);
        customRecurringLayout = view.findViewById(R.id.customRecurringLayout);
        recurringInterval = view.findViewById(R.id.recurringInterval);
        recurringUnit = view.findViewById(R.id.recurringUnit);

        weekDayCheckboxes = new CheckBox[7];
        weekDayCheckboxes[0] = view.findViewById(R.id.mondayCheck);
        weekDayCheckboxes[1] = view.findViewById(R.id.tuesdayCheck);
        weekDayCheckboxes[2] = view.findViewById(R.id.wednesdayCheck);
        weekDayCheckboxes[3] = view.findViewById(R.id.thursdayCheck);
        weekDayCheckboxes[4] = view.findViewById(R.id.fridayCheck);
        weekDayCheckboxes[5] = view.findViewById(R.id.saturdayCheck);
        weekDayCheckboxes[6] = view.findViewById(R.id.sundayCheck);

        List<String> units = Arrays.stream(TimeUnit.values())
                .map(TimeUnit::getLabel)
                .collect(Collectors.toUnmodifiableList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                units
        );
        recurringUnit.setAdapter(adapter);

        // Set up listeners
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

            recurringPattern = eventToEdit.getRecurrencePattern();
            Log.d("test", recurringPattern.toString());
            setupRecurringUI();
        } else {
            recurringPattern = new RecurrencePattern();
        }

        Button datePickerButton = view.findViewById(R.id.datePickerButton);
        datePickerButton.setOnClickListener(v -> showDatePicker());

        Button timePickerButton = view.findViewById(R.id.timePickerButton);
        timePickerButton.setOnClickListener(v -> showTimePicker());

        Button saveButton = view.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveEvent());

        return view;
    }

    private void setupRecurringListeners() {
        isRecurringSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recurringOptionsLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            recurringPattern.setRecurrenceEnabled(recurringPattern.getRecurrenceEnabled() == null || !recurringPattern.getRecurrenceEnabled());
        });

        recurringTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.weeklyRecurring) {
                weekDaysLayout.setVisibility(View.VISIBLE);
                customRecurringLayout.setVisibility(View.GONE);
                recurringPattern.setType(RecurrenceType.WEEKLY);
            } else if (checkedId == R.id.customRecurring) {
                weekDaysLayout.setVisibility(View.GONE);
                customRecurringLayout.setVisibility(View.VISIBLE);
                recurringPattern.setType(RecurrenceType.CUSTOM);
            }
        });

        // Set up weekday checkbox listeners
        for (int i = 0; i < weekDayCheckboxes.length; i++) {
            final DayOfWeek day = DayOfWeek.of(i + 1);
            weekDayCheckboxes[i].setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    WeekDaysBooleanWrapper wrapper = new WeekDaysBooleanWrapper();
                    wrapper.setDayOfWeek(PolishDayOfWeek.fromDayOfWeek(day));
                    wrapper.setActive(true);
                    recurringPattern.getWeekDays().add(wrapper);
                } else {
                    WeekDaysBooleanWrapper wrapper = new WeekDaysBooleanWrapper();
                    wrapper.setDayOfWeek(PolishDayOfWeek.fromDayOfWeek(day));
                    wrapper.setActive(true);
                    recurringPattern.getWeekDays().get(recurringPattern.getWeekDays().indexOf(wrapper)).setActive(false);
                }
            });
        }

        recurringUnit.setOnItemClickListener((parent, view, position, id) -> {
            TimeUnit selectedUnit = TimeUnit.values()[position];
            recurringPattern.setTimeUnit(selectedUnit);
        });
    }

    private void setupRecurringUI() {
        Log.d("test", recurringPattern.toString());
        if (recurringPattern != null) {
            boolean isRecurring = recurringPattern.getRecurrenceEnabled();
            isRecurringSwitch.setChecked(isRecurring);
            recurringOptionsLayout.setVisibility(isRecurring ? View.VISIBLE : View.GONE);

            if (isRecurring) {
                boolean b = recurringPattern.getType() == RecurrenceType.CUSTOM;
                Log.d("test", "is " + recurringPattern.getType() + " equal to " + RecurrenceType.CUSTOM + " ? " + b);

                if (recurringPattern.getWeekDays() != null) {
                    Log.d("test", "here2");

                    recurringTypeGroup.check(R.id.weeklyRecurring);
                    weekDaysLayout.setVisibility(View.VISIBLE);
                    customRecurringLayout.setVisibility(View.GONE);

                    // Set weekday checkboxes
                    for (int i = 0; i < weekDayCheckboxes.length; i++) {
                        DayOfWeek day = DayOfWeek.of(i + 1);
                        PolishDayOfWeek polishDayOfWeek = PolishDayOfWeek.fromDayOfWeek(day);
                        weekDayCheckboxes[i].setChecked(recurringPattern.getWeekDays().stream().anyMatch(d -> d.getDayOfWeek() == polishDayOfWeek));
                    }
                } else if (recurringPattern.getType() == RecurrenceType.CUSTOM) {
                    Log.d("test", "here3");
                    recurringTypeGroup.check(R.id.customRecurring);
                    weekDaysLayout.setVisibility(View.GONE);
                    customRecurringLayout.setVisibility(View.VISIBLE);

                    recurringInterval.setText(String.valueOf(recurringPattern.getInterval()));
                    recurringUnit.setText(recurringPattern.getTimeUnit().getLabel(), false);
                }
            }
        }
    }

    private void saveEvent() {
        String title = editTitle.getText().toString();
        String place = editPlace.getText().toString();

        if (title.isEmpty() || selectedDate == null) {
            Toast.makeText(requireContext(), "Wypełnij wymagany tytuł oraz datę", Toast.LENGTH_LONG).show();
            return;
        }

        if (isRecurringSwitch.isChecked()) {
            if (recurringPattern.getType() == RecurrenceType.WEEKLY &&
                    recurringPattern.getWeekDays().isEmpty()) {
                Toast.makeText(requireContext(), "Wybierz przynajmniej jeden dzień tygodnia", Toast.LENGTH_LONG).show();
                return;
            } else if (recurringPattern.getType() == RecurrenceType.CUSTOM) {
                String intervalStr = recurringInterval.getText().toString();
                if (intervalStr.isEmpty() || recurringUnit.getText().toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Wypełnij interwał i jednostkę czasu", Toast.LENGTH_LONG).show();
                    return;
                }
                recurringPattern.setInterval(Integer.parseInt(intervalStr));
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
            event.setRecurrencePattern(recurringPattern);
            eventDao.update(event);
        } else {
            event = new Event(title, place, selectedDate, selectedTime,
                    isImportantSwitch.isChecked() ? Priority.IMPORTANT : Priority.NORMAL);
            event.setRecurrencePattern(recurringPattern);
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
