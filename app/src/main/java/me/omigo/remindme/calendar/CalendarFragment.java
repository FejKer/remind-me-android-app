package me.omigo.remindme.calendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import me.omigo.remindme.AppDatabase;
import me.omigo.remindme.R;
import me.omigo.remindme.events.Event;
import me.omigo.remindme.events.EventDao;
import me.omigo.remindme.events.EventDialogAdapter;
import me.omigo.remindme.events.EventDialogFragment;
import me.omigo.remindme.events.Priority;
import me.omigo.remindme.events.RecurringEventCalculator;

public class CalendarFragment extends Fragment implements EventDialogFragment.EventDialogListener {
    //private RecyclerView recyclerView;
    private AppDatabase appDatabase;
    private EventDao eventDao;
    //private RecyclerViewAdapter recyclerViewAdapter;
    //private CustomCalendarView calendarView;

    private Calendar currentMonth;
    private List<CalendarAndIsImportantWrapper> markedDates;
    private Button prevMonthButton;
    private Button nextMonthButton;
    private CalendarAdapter adapter;
    private GridView calendarGridView;
    private TextView monthYearText;
    private RecyclerView eventsRecyclerView;
    private Calendar selectedDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        appDatabase = AppDatabase.getDatabase(requireContext());
        eventDao = appDatabase.eventDao();

        monthYearText = view.findViewById(R.id.monthYearText);
        calendarGridView = view.findViewById(R.id.calendarGridView);
        prevMonthButton = view.findViewById(R.id.prevMonthButton);
        nextMonthButton = view.findViewById(R.id.nextMonthButton);

        currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.DAY_OF_MONTH, 1);

        monthYearText.setText(getMonthYear(currentMonth));

        markedDates = getDates();

        // Set up the adapter
        adapter = new CalendarAdapter(requireContext(), currentMonth, markedDates, date -> {
            this.selectedDate = date;
            showEventsDialog(date);
            //updateEvents(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
        });
        calendarGridView.setAdapter(adapter);

        //calendarView = view.findViewById(R.id.calendarView);

//        recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

//        recyclerViewAdapter = new RecyclerViewAdapter(eventDao.getEventsByDate(LocalDate.now().toEpochDay()));
//        recyclerView.setAdapter(recyclerViewAdapter);

        setUpButtons();
        return view;
    }

    private List<CalendarAndIsImportantWrapper> getDates() {
        List<Event> allEvents = eventDao.getAllEvents();
        List<CalendarAndIsImportantWrapper> calendars = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // First handle non-recurring events
        for (Event event : allEvents) {
            if (!event.getRecurring()) {
                Calendar calendar = getCalendarDate(
                        event.getDate().getYear(),
                        event.getDate().getMonthValue() - 1,
                        event.getDate().getDayOfMonth()
                );
                calendars.add(new CalendarAndIsImportantWrapper(
                        calendar,
                        event.getPriority() == Priority.IMPORTANT
                ));
            }
        }

        // Handle recurring events
        for (Event event : allEvents) {
            if (event.getRecurring()) {
                // Generate instances for 3 months before and 3 months after current date
                LocalDate startDate = today.minusYears(3);
                LocalDate endDate = today.plusYears(3);

                List<Event> recurringInstances = RecurringEventCalculator
                        .generateRecurringEventInstances(event, startDate, endDate)
                        .stream()
                       // .filter(e -> !(event.getId() == e.getParentEventId()
                       //         && event.getDate().equals(e.getDate())))
                        .collect(Collectors.toList());

                for (Event instance : recurringInstances) {
                    Calendar calendar = getCalendarDate(
                            instance.getDate().getYear(),
                            instance.getDate().getMonthValue() - 1,
                            instance.getDate().getDayOfMonth()
                    );
                    calendars.add(new CalendarAndIsImportantWrapper(
                            calendar,
                            instance.getPriority() == Priority.IMPORTANT
                    ));
                }
            }
        }

        return calendars;
    }

    private void setUpButtons() {
//        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
//            String selectedDate = "Selected Date: " + (month + 1) + "/" + dayOfMonth + "/" + year;
//            Toast.makeText(requireContext(), selectedDate, Toast.LENGTH_SHORT).show();
//            updateEvents(year, month + 1, dayOfMonth);
//        });
        prevMonthButton.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            adapter = new CalendarAdapter(requireContext(), currentMonth, markedDates, adapter.dateClickListener);
            calendarGridView.setAdapter(adapter);
            updateMonthYearText(monthYearText);
        });

        // Handle next month button click
        nextMonthButton.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            adapter = new CalendarAdapter(requireContext(), currentMonth, markedDates, adapter.dateClickListener);
            calendarGridView.setAdapter(adapter);
            updateMonthYearText(monthYearText);
        });
    }

//    private void updateEvents(Integer year, Integer month, Integer dayOfMonth) {
//        recyclerViewAdapter.removeEvents();
//        Long now = LocalDate.of(year, month, dayOfMonth).toEpochDay();
//        List<Event> events = eventDao.getEventsByDate(now);
//        for (var event : events) {
//            recyclerViewAdapter.updateEvents(event);
//        }
//    }

    private void updateMonthYearText(TextView monthYearText) {
        monthYearText.setText(currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale)
                + " " + currentMonth.get(Calendar.YEAR));
    }

    private String getMonthYear(Calendar calendar) {
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale)
                + " " + calendar.get(Calendar.YEAR);
    }

    private Calendar getCalendarDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar;
    }

    private void showEventsDialog(Calendar selectedDate) {
        // Convert Calendar to LocalDate
        LocalDate date = LocalDate.of(
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH) + 1,
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        Log.d("recurring", "selected date " + date);

        // Get regular events for the selected date
        List<Event> events = new ArrayList<>(eventDao.getEventsByDateAndNotRecurring(date.toEpochDay()));

        // Get recurring events
        List<Event> allEvents = eventDao.getAllEvents();
        for (Event event : allEvents) {
            if (event.getRecurring()) {
                List<Event> instances = RecurringEventCalculator
                        .generateRecurringEventInstances(event, date, LocalDate.from(date.atTime(23,59,59,999999999)))
                        .stream()
                        .collect(Collectors.toList());
                Log.d("recurring", "instances " + instances);
                events.addAll(instances);
            }
        }

        // Sort events by time
        events.sort((e1, e2) -> {
            if (e1.getTime() == null && e2.getTime() == null) return 0;
            if (e1.getTime() == null) return -1;
            if (e2.getTime() == null) return 1;
            return e1.getTime().compareTo(e2.getTime());
        });

        if (events.isEmpty()) {
            Toast.makeText(requireContext(), "Brak wydarzeń tego dnia", Toast.LENGTH_LONG).show();
            return;
        }

        // Create and configure the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.event_calendar_dialog, null);

        // Set up RecyclerView in the dialog
        eventsRecyclerView = dialogView.findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        EventDialogAdapter dialogAdapter = new EventDialogAdapter(events);
        dialogAdapter.setOnEventEditListener(event -> {
            EventDialogFragment dialogFragment = new EventDialogFragment();
            dialogFragment.setEventToEdit(event);
            dialogFragment.setEventDialogListener(this);
            dialogFragment.show(getParentFragmentManager(), "EventDialogFragment");
        });
        eventsRecyclerView.setAdapter(dialogAdapter);

        // Set up dialog title
        TextView dialogTitleText = dialogView.findViewById(R.id.dialogTitleText);
        dialogTitleText.setText(String.format("Wydarzenia %d.%d.%d",
                selectedDate.get(Calendar.DAY_OF_MONTH),
                selectedDate.get(Calendar.MONTH) + 1,
                selectedDate.get(Calendar.YEAR)
        ));

        // Build and show the dialog
        builder.setView(dialogView)
                .setNegativeButton("Zamknij", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Make dialog fill width and center
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.CENTER);

        dialog.show();
    }

    @Override
    public void onEventSaved(Event event) {
        LocalDate localDate = LocalDate.of(selectedDate.get(Calendar.YEAR), (selectedDate.get(Calendar.MONTH) + 1), selectedDate.get(Calendar.DAY_OF_MONTH));
        if (eventsRecyclerView != null && eventsRecyclerView.getAdapter() != null && event.getDate().equals(localDate)) {
            ((EventDialogAdapter) eventsRecyclerView.getAdapter()).updateEvents(event);
        }

        markedDates = getDates();

        adapter = new CalendarAdapter(requireContext(), currentMonth, markedDates, adapter.dateClickListener);
        calendarGridView.setAdapter(adapter);
    }

    @Override
    public void onSlaveEventsDeleted(long id) {
        if (eventsRecyclerView != null && eventsRecyclerView.getAdapter() != null) {
            ((EventDialogAdapter) eventsRecyclerView.getAdapter()).deleteSlaveEvents(id);
        }

        markedDates = getDates();

        adapter = new CalendarAdapter(requireContext(), currentMonth, markedDates, adapter.dateClickListener);
        calendarGridView.setAdapter(adapter);
    }
}
