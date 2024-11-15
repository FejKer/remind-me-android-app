package me.omigo.remindme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        view = (RecyclerView) findViewById(R.id.recyclerView);
//
//        view.setLayoutManager(new LinearLayoutManager(this));
//
//        appDatabase = AppDatabase.getDatabase(getApplicationContext());
//        eventDao = appDatabase.eventDao();
//
//        recyclerViewAdapter = new RecyclerViewAdapter(eventDao.getAllEvents());
//
//        view.setAdapter(recyclerViewAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        loadFragment(new ListFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment;
                if (tab.getPosition() == 0) {
                    selectedFragment = new ListFragment();
                } else {
                    selectedFragment = new CalendarFragment();
                }
                loadFragment(selectedFragment);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
//
//    @Override
//    public void onEventSaved(Event event) {
//        recyclerViewAdapter.updateEvents(event);
//    }
}