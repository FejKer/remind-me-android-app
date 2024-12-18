package me.omigo.remindme;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

        pastEventsAdapter = new RecyclerViewAdapter(new ArrayList<>());
        upcomingEventsAdapter = new RecyclerViewAdapter(new ArrayList<>());
        futureEventsAdapter = new RecyclerViewAdapter(new ArrayList<>());

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

        List<Event> pastEvents = new ArrayList<>();
        List<Event> upcomingEvents = new ArrayList<>();
        List<Event> futureEvents = new ArrayList<>();

        for (Event event : allEvents) {
            LocalDate eventDate = event.getDate();
            if (eventDate.isBefore(today)) {
                pastEvents.add(event);
            } else if (eventDate.isBefore(oneMonthFromNow) || eventDate.isEqual(oneMonthFromNow)) {
                upcomingEvents.add(event);
            } else {
                futureEvents.add(event);
            }
        }

        pastEvents.forEach(pastEventsAdapter::updateEvents);
        upcomingEvents.forEach(upcomingEventsAdapter::updateEvents);
        futureEvents.forEach(futureEventsAdapter::updateEvents);
    }

    @Override
    public void onEventSaved(Event event) {
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