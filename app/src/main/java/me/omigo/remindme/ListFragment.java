package me.omigo.remindme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class ListFragment extends Fragment implements EventDialogFragment.EventDialogListener {
    private RecyclerView recyclerView;
    private AppDatabase appDatabase;
    private EventDao eventDao;
    private RecyclerViewAdapter recyclerViewAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        appDatabase = AppDatabase.getDatabase(requireContext());
        eventDao = appDatabase.eventDao();

        recyclerViewAdapter = new RecyclerViewAdapter(eventDao.getAllEvents());
        recyclerViewAdapter.setOnEventEditListener(event -> {
            // Handle edit event here
            EventDialogFragment dialogFragment = new EventDialogFragment();
            // Set up dialog with event data
            dialogFragment.show(getParentFragmentManager(), "EventDialogFragment");
        });
        recyclerView.setAdapter(recyclerViewAdapter);

        setUpButtons(view);

        return view;
    }

    private void setUpButtons(View view) {
        view.findViewById(R.id.fabAdd).setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            EventDialogFragment eventDialogFragment = new EventDialogFragment();
            eventDialogFragment.setEventDialogListener(this);
            eventDialogFragment.show(fragmentManager, "EventDialogFragment");
        });
        view.findViewById(R.id.buttonClearDb).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "DUPA", Toast.LENGTH_SHORT);
            eventDao.purgeDb();
            recyclerViewAdapter.removeEvents();
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

            LocalDate localDate = LocalDate.of(2024, 11, random.nextInt(30) + 1);
            LocalTime localTime = LocalTime.of(random.nextInt(23) + 1, random.nextInt(59) + 1);

            Boolean isImportant = random.nextBoolean();

            event.setDate(localDate);
            event.setTime(localTime);
            event.setPlace(generatedString2);
            event.setTitle(generatedString);
            event.setPriority(isImportant ? Priority.IMPORTANT : Priority.NORMAL);

            eventDao.insert(event);
            onEventSaved(event);
        });
    }

    @Override
    public void onEventSaved(Event event) {
        recyclerViewAdapter.updateEvents(event);
    }
}
