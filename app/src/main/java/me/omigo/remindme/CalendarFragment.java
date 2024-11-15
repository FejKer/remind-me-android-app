package me.omigo.remindme;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;

public class CalendarFragment extends Fragment {
    private RecyclerView recyclerView;
    private AppDatabase appDatabase;
    private EventDao eventDao;
    private RecyclerViewAdapter recyclerViewAdapter;
    private CalendarView calendarView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        calendarView = view.findViewById(R.id.calendarView);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        appDatabase = AppDatabase.getDatabase(requireContext());
        eventDao = appDatabase.eventDao();

        recyclerViewAdapter = new RecyclerViewAdapter(eventDao.getEventsByDate(LocalDate.now().toEpochDay()));
        recyclerView.setAdapter(recyclerViewAdapter);

        setUpButtons();
        return view;
    }

    private void setUpButtons() {
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String selectedDate = "Selected Date: " + (month + 1) + "/" + dayOfMonth + "/" + year;
            Toast.makeText(requireContext(), selectedDate, Toast.LENGTH_SHORT).show();
            updateEvents(year, month + 1, dayOfMonth);
        });
    }

    private void updateEvents(Integer year, Integer month, Integer dayOfMonth) {
        recyclerViewAdapter.removeEvents();
        Log.i("INFO", String.valueOf(year));
        Log.i("INFO", String.valueOf(month));
        Log.i("INFO", String.valueOf(dayOfMonth));
        Long now = LocalDate.of(year, month, dayOfMonth).toEpochDay();
        Log.i("INFO", now.toString());
        List<Event> events = eventDao.getEventsByDate(now);
        for (var event : events) {
            recyclerViewAdapter.updateEvents(event);
        }
    }
}
