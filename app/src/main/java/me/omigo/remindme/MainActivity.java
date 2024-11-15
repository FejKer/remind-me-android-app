package me.omigo.remindme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        findViewById(R.id.button).setOnClickListener(v -> {
            FragmentManager fragmentManager = getSupportFragmentManager();
            EventDialogFragment eventDialogFragment = new EventDialogFragment();
            eventDialogFragment.setEventDialogListener(this);
            eventDialogFragment.show(fragmentManager, "EventDialogFragment");
        });
    }

    @Override
    public void onEventSaved(Event event) {
        recyclerViewAdapter.updateEvents(event);
    }
}