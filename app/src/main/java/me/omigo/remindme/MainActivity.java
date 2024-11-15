package me.omigo.remindme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements EventDialogFragment.EventDialogListener {

    private RecyclerView view;
    private AppDatabase appDatabase;
    private EventDao eventDao;
    private RecyclerViewAdapter recyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (RecyclerView) findViewById(R.id.recyclerView);

        view.setLayoutManager(new LinearLayoutManager(this));

        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        eventDao = appDatabase.eventDao();

        recyclerViewAdapter = new RecyclerViewAdapter(eventDao.getAllEvents());

        view.setAdapter(recyclerViewAdapter);
        setUpButtons();
    }

    private void setUpButtons() {
        findViewById(R.id.button).setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            EventDialogFragment eventDialogFragment = new EventDialogFragment();
            eventDialogFragment.setEventDialogListener(this);
            eventDialogFragment.show(fragmentManager, "EventDialogFragment");
        });
        findViewById(R.id.buttonClearDb).setOnClickListener(v -> {
            eventDao.purgeDb();
            recyclerViewAdapter.removeEvents();
        });
        findViewById(R.id.addRandomButton).setOnClickListener(v -> {
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

            LocalDate localDate = LocalDate.of(random.nextInt(2500) + 1, random.nextInt(12) + 1, random.nextInt(28) + 1);
            LocalTime localTime = LocalTime.of(random.nextInt(23) + 1, random.nextInt(59) + 1);

            Boolean isImportant = random.nextBoolean();

            event.setDate(localDate);
            event.setTime(localTime);
            event.setPlace(generatedString2);
            event.setTitle(generatedString);
            event.setPriority(isImportant ? Priority.IMPORTANT : Priority.NORMAl);

            eventDao.insert(event);
            onEventSaved(event);
        });
        findViewById(R.id.calendarTabItem).setOnClickListener(v -> {
//            Intent intent = new Intent(this, CalendarActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//            startActivity(intent);
        });
    }

    @Override
    public void onEventSaved(Event event) {
        recyclerViewAdapter.updateEvents(event);
    }
}