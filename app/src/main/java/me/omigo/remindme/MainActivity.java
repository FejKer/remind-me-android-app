package me.omigo.remindme;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import me.omigo.remindme.calendar.CalendarFragment;
import me.omigo.remindme.listview.ListFragment;
import me.omigo.remindme.recurring.RecurringEventsFragment;
import me.omigo.remindme.screensaver.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
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
                } else if (tab.getPosition() == 1){
                    selectedFragment = new CalendarFragment();
                } else {
                    selectedFragment = new RecurringEventsFragment();
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