package me.omigo.remindme.listview;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import me.omigo.remindme.AppDatabase;
import me.omigo.remindme.events.Event;
import me.omigo.remindme.events.EventDao;
import me.omigo.remindme.events.EventDialogFragment;
import me.omigo.remindme.events.Priority;
import me.omigo.remindme.R;
import me.omigo.remindme.events.RecurringEventCalculator;

public class ListFragment extends Fragment implements EventDialogFragment.EventDialogListener {
    private TextView currentEventsTextView;
    private RecyclerView pastEventsRecyclerView;
    private RecyclerView upcomingEventsRecyclerView;
    private RecyclerView futureEventsRecyclerView;
    private NestedScrollView scrollView;
    private AppDatabase appDatabase;
    private EventDao eventDao;
    private RecyclerViewAdapter pastEventsAdapter;
    private RecyclerViewAdapter upcomingEventsAdapter;
    private RecyclerViewAdapter futureEventsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        initializeViews(view);
        setupRecyclerViews();
        setUpButtons(view);
        updateEvents();

        // Scroll to upcoming events section after layout is complete
        view.post(() -> {
            int upcomingEventsPosition = currentEventsTextView.getTop();
            scrollView.smoothScrollTo(0, upcomingEventsPosition);
        });

        return view;
    }

    private void initializeViews(View view) {
        scrollView = view.findViewById(R.id.scrollView);
        pastEventsRecyclerView = view.findViewById(R.id.pastEventsRecyclerView);
        upcomingEventsRecyclerView = view.findViewById(R.id.upcomingEventsRecyclerView);
        futureEventsRecyclerView = view.findViewById(R.id.futureEventsRecyclerView);
        currentEventsTextView = view.findViewById(R.id.currentEventsLabel);
        appDatabase = AppDatabase.getDatabase(requireContext());
        eventDao = appDatabase.eventDao();
    }

    private void setupRecyclerViews() {
        setupRecyclerView(pastEventsRecyclerView);
        setupRecyclerView(upcomingEventsRecyclerView);
        setupRecyclerView(futureEventsRecyclerView);

        pastEventsAdapter = new RecyclerViewAdapter(new ArrayList<>(), getContext());
        upcomingEventsAdapter = new RecyclerViewAdapter(new ArrayList<>(), getContext());
        futureEventsAdapter = new RecyclerViewAdapter(new ArrayList<>(), getContext());

        setupAdapter(pastEventsAdapter);
        setupAdapter(upcomingEventsAdapter);
        setupAdapter(futureEventsAdapter);

        pastEventsRecyclerView.setAdapter(pastEventsAdapter);
        upcomingEventsRecyclerView.setAdapter(upcomingEventsAdapter);
        futureEventsRecyclerView.setAdapter(futureEventsAdapter);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupAdapter(RecyclerViewAdapter adapter) {
        adapter.setOnEventEditListener(event -> {
            EventDialogFragment dialogFragment = new EventDialogFragment();
            dialogFragment.setEventToEdit(event);
            dialogFragment.setEventDialogListener(this);
            dialogFragment.show(getParentFragmentManager(), "EventDialogFragment");
        });
    }

    private void updateEvents() {
        List<Event> allEvents = eventDao.getAllEvents();
        LocalDate today = LocalDate.now();
        LocalDate oneMonthFromNow = today.plusMonths(1);
        LocalDate threeMonthsFromNow = today.plusMonths(3); // For future events calculation

        List<Event> pastEvents = new ArrayList<>();
        List<Event> upcomingEvents = new ArrayList<>();
        List<Event> futureEvents = new ArrayList<>();

        // First, separate regular events
        for (Event event : allEvents) {
            if (!event.getRecurring()) {
                Log.d("recurring", "parsing non recurring event " + event);
                categorizeEvent(event, today, oneMonthFromNow, pastEvents, upcomingEvents, futureEvents);
            }
        }

        // Handle recurring events
        for (Event event : allEvents) {
            if (event.getRecurring()) {
                Log.d("recurring", "parsing recurring event " + event);
                categorizeEvent(event, today, oneMonthFromNow, pastEvents, upcomingEvents, futureEvents);

                // Generate past instances
                List<Event> pastInstances = RecurringEventCalculator.generateRecurringEventInstances(
                                event,
                                today.minusMonths(3), // Show past 3 months of recurring events
                                today
                        ).stream()
                        .filter(e -> !(event.getId() == e.getParentEventId() && event.getDate().equals(e.getDate())))
                        .collect(Collectors.toUnmodifiableList());
                Log.d("recurring", "past " + pastInstances);
                pastEvents.addAll(pastInstances);

                // Generate upcoming instances
                List<Event> upcomingInstances = RecurringEventCalculator.generateRecurringEventInstances(
                        event,
                        today,
                        oneMonthFromNow
                ).stream()
                        .filter(e -> !(event.getId() == e.getParentEventId() && event.getDate().equals(e.getDate())))
                        .collect(Collectors.toUnmodifiableList());
                Log.d("recurring", "upcomingInstances " + upcomingInstances);
                upcomingEvents.addAll(upcomingInstances);

                // Generate future instances
                List<Event> futureInstances = RecurringEventCalculator.generateRecurringEventInstances(
                        event,
                        oneMonthFromNow,
                        threeMonthsFromNow
                ).stream()
                        .filter(e -> !(event.getId() == e.getParentEventId() && event.getDate().equals(e.getDate())))
                        .collect(Collectors.toUnmodifiableList());
                Log.d("recurring", "futureInstances " + futureInstances);
                futureEvents.addAll(futureInstances);
            }
        }

        // Update adapters
        updateAdapter(pastEventsAdapter, pastEvents);
        updateAdapter(upcomingEventsAdapter, upcomingEvents);
        updateAdapter(futureEventsAdapter, futureEvents);
    }

    private void categorizeEvent(Event event, LocalDate today, LocalDate oneMonthFromNow,
                                 List<Event> pastEvents, List<Event> upcomingEvents, List<Event> futureEvents) {
        LocalDate eventDate = event.getDate();
        if (eventDate.isBefore(today)) {
            pastEvents.add(event);
        } else if (eventDate.isBefore(oneMonthFromNow) || eventDate.isEqual(oneMonthFromNow)) {
            upcomingEvents.add(event);
        } else {
            futureEvents.add(event);
        }
    }

    private void updateAdapter(RecyclerViewAdapter adapter, List<Event> events) {
        adapter.removeEvents(); // Clear existing events
        for (Event event : events) {
            Log.d("recurring", "updating for event " + event);
            adapter.updateEvents(event);
        }
    }


    @Override
    public void onEventSaved(Event event) {
        Log.d("recurring", "event " + event);
        updateEvents();
    }

    @Override
    public void onSlaveEventsDeleted(long id) {
        //pastEventsAdapter.deleteSlaveEvents(id);
        //futureEventsAdapter.deleteSlaveEvents(id);
        //upcomingEventsAdapter.deleteSlaveEvents(id);
        updateEvents();
    }

    private void setUpButtons(View view) {
        view.findViewById(R.id.fabAdd).setOnClickListener(v -> {
            EventDialogFragment dialogFragment = new EventDialogFragment();
            dialogFragment.setEventDialogListener(this);
            dialogFragment.show(getParentFragmentManager(), "EventDialogFragment");
        });

        view.findViewById(R.id.buttonClearDb).setOnClickListener(v -> {
            eventDao.purgeDb();
            futureEventsAdapter.removeEvents();
            pastEventsAdapter.removeEvents();
            upcomingEventsAdapter.removeEvents();
        });

        view.findViewById(R.id.addRandomButton).setOnClickListener(v -> {
            Event event = new Event();
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();

            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            String generatedString2 = random.ints(leftLimit, rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            LocalDate localDate = LocalDate.of(random.nextInt(2) + 2024, random.nextInt(3) + 10, random.nextInt(30) + 1);
            LocalTime localTime = LocalTime.of(random.nextInt(23) + 1, random.nextInt(59) + 1);

            Boolean isImportant = random.nextBoolean();

            event.setDate(localDate);
            event.setTime(localTime);
            event.setPlace(generatedString2);
            event.setTitle(generatedString);
            event.setPriority(isImportant ? Priority.IMPORTANT : Priority.NORMAL);

            long insert = eventDao.insert(event);
            event.setId(insert);
            onEventSaved(event);
        });
    }
}