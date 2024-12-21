package me.omigo.remindme.recurring;

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

import java.util.ArrayList;
import java.util.List;

import me.omigo.remindme.AppDatabase;
import me.omigo.remindme.R;
import me.omigo.remindme.events.Event;
import me.omigo.remindme.events.EventDao;
import me.omigo.remindme.events.EventDialogFragment;
import me.omigo.remindme.listview.RecyclerViewAdapter;

public class RecurringEventsFragment extends Fragment implements EventDialogFragment.EventDialogListener {
    private RecyclerView recurringEventsRecyclerView;
    private NestedScrollView scrollView;
    private AppDatabase appDatabase;
    private EventDao eventDao;
    private RecyclerViewAdapter recurringEventsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recurring_list, container, false);

        initializeViews(view);
        setupRecyclerView();
        setUpButtons(view);
        updateEvents();

        return view;
    }

    private void initializeViews(View view) {
        scrollView = view.findViewById(R.id.scrollView);
        recurringEventsRecyclerView = view.findViewById(R.id.recurringEventsRecyclerView);
        appDatabase = AppDatabase.getDatabase(requireContext());
        eventDao = appDatabase.eventDao();
    }

    private void setupRecyclerView() {
        recurringEventsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recurringEventsAdapter = new RecyclerViewAdapter(new ArrayList<>(), getContext());
        setupAdapter(recurringEventsAdapter);
        recurringEventsRecyclerView.setAdapter(recurringEventsAdapter);
    }

    private void setupAdapter(RecyclerViewAdapter adapter) {
        adapter.setOnEventEditListener(event -> {
            EventDialogFragment dialogFragment = new EventDialogFragment();
            dialogFragment.setEventToEdit(event);
            dialogFragment.setEventDialogListener(this);
            dialogFragment.show(getParentFragmentManager(), "EventDialogFragment");
        });

        adapter.setOnEventDeleteListener(event -> {
            eventDao.delete(event.getId());

            //adapter.deleteEvent(event);

            updateEvents();
        });
    }

    private void updateEvents() {
        List<Event> recurringEvents = eventDao.getAllRecurringEvents();
        updateAdapter(recurringEventsAdapter, recurringEvents);
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
        updateEvents();
    }

    private void setUpButtons(View view) {
//        view.findViewById(R.id.fabAddRecurring).setOnClickListener(v -> {
//            EventDialogFragment dialogFragment = new EventDialogFragment();
//            dialogFragment.setEventDialogListener(this);
//            dialogFragment.show(getParentFragmentManager(), "EventDialogFragment");
//        });
    }
}