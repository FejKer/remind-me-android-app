package me.omigo.remindme;

import static me.omigo.remindme.R.id.recyclerView;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        view = (RecyclerView) findViewById(R.id.recyclerView);

        view.setLayoutManager(new LinearLayoutManager(this));

        Event event1 = new Event("Lekarz", "Polmed", LocalDate.now(), LocalTime.now());
        Event event2 = new Event("Fryzjer", "Forum Gdańsk", LocalDate.now(), LocalTime.now());
        Event event3 = new Event("Ruchanie dupy", "GOGO", LocalDate.now(), LocalTime.now());

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(List.of(event1, event2, event3));

        view.setAdapter(recyclerViewAdapter);
    }
}