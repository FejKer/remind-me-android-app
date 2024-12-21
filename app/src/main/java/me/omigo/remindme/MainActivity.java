package me.omigo.remindme;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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

        View rootLayout = findViewById(android.R.id.content);

        setupTouchListener(rootLayout);
    }

    private void setupTouchListener(View view) {
        // Check if the view is not an EditText
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard();
                return false;
            });
        }

        // If the view is a ViewGroup and not a RecyclerView or ScrollView,
        // set up touch listeners for its children
        if (view instanceof ViewGroup && !(view instanceof RecyclerView)
                && !(view instanceof ScrollView)) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View innerView = viewGroup.getChildAt(i);
                setupTouchListener(innerView);
            }
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            // Clear focus from the input field
            view.clearFocus();
        }
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