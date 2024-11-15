package me.omigo.remindme;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarActivity extends AppCompatActivity {
    private RecyclerView view;
    private AppDatabase appDatabase;
    private EventDao eventDao;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view);

        view = (RecyclerView) findViewById(R.id.calendarRecyclerView);

        view.setLayoutManager(new LinearLayoutManager(this));

        appDatabase = AppDatabase.getDatabase(getApplicationContext());
        eventDao = appDatabase.eventDao();

        recyclerViewAdapter = new RecyclerViewAdapter(eventDao.getAllEvents());

        view.setAdapter(recyclerViewAdapter);

        setUpButtons();
    }

    private void setUpButtons() {
        findViewById(R.id.listTabItem).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }
}
