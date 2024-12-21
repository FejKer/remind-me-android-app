package me.omigo.remindme.screensaver;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import me.omigo.remindme.AppDatabase;
import me.omigo.remindme.R;
import me.omigo.remindme.events.Event;
import me.omigo.remindme.events.EventDao;
import me.omigo.remindme.events.RecurringEventCalculator;

public class EventScreenSaverActivity extends BaseActivity {
    private TextView clockTextView;
    private TextView dateTextView;
    private TextView moreEventsTextView;
    private RecyclerView eventsRecyclerView;
    private ConstraintLayout backgroundLayout;
    private Handler updateHandler;
    private static final long UPDATE_INTERVAL = 60000; // Update every minute
    private EventDao eventDao;
    private ScreenSaverEventAdapter eventAdapter;
    private static final int MAX_VISIBLE_EVENTS = 3;
    private static final DateTimeFormatter CLOCK_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");

    private BackgroundColorListener listener;

    public interface BackgroundColorListener {
        void setColor(int textColor);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set window flags for fullscreen and keep screen on
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        // Hide system UI for true fullscreen experience
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_event_screen_saver);

        // Initialize views
        clockTextView = findViewById(R.id.clockTextView);
        dateTextView = findViewById(R.id.dateTextView);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        moreEventsTextView = findViewById(R.id.moreEventsTextView);
        backgroundLayout = findViewById(R.id.backgroundLayout);

        // Initialize RecyclerView
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new ScreenSaverEventAdapter(new ArrayList<>());
        listener = eventAdapter;
        eventsRecyclerView.setAdapter(eventAdapter);

        eventDao = AppDatabase.getDatabase(getApplicationContext()).eventDao();

        View rootView = findViewById(android.R.id.content);
        rootView.setOnClickListener(v -> finish());

        // Set up periodic updates
        updateHandler = new Handler();
        startUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDisplay();
        startUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUpdates();
    }

    private void startUpdates() {
        updateRunnable.run();
    }

    private void stopUpdates() {
        updateHandler.removeCallbacks(updateRunnable);
    }

    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            updateDisplay();
            updateHandler.postDelayed(this, UPDATE_INTERVAL);
        }
    };

    @Override
    public void resetInactivityTimer() {
        // Do nothing - prevent starting another screen saver
    }

    private void updateDisplay() {
        // Update clock and date
        LocalDateTime now = LocalDateTime.now();
        clockTextView.setText(CLOCK_FORMATTER.format(now));
        dateTextView.setText(DATE_FORMATTER.format(now));

        List<Event> upcomingEvents = getUpcomingEvents();

        // Add recurring events
        for (var event : eventDao.getAllRecurringEvents()) {
            var recurringEvents = RecurringEventCalculator.generateRecurringEventInstances(event, LocalDate.now(), LocalDate.now().plusDays(3));
            recurringEvents = recurringEvents
                    .stream()
                    .filter(e -> !e.getHiddenFromScreenSaver())
                    .collect(Collectors.toUnmodifiableList());
            upcomingEvents.addAll(recurringEvents);
        }

        // Sort events by date and time
        upcomingEvents.sort((e1, e2) -> {
            LocalDateTime dt1 = LocalDateTime.of(e1.getDate(), Optional.ofNullable(e1.getTime()).orElse(LocalTime.MIDNIGHT));
            LocalDateTime dt2 = LocalDateTime.of(e2.getDate(), Optional.ofNullable(e2.getTime()).orElse(LocalTime.MIDNIGHT));
            return dt1.compareTo(dt2);
        });

        // Update RecyclerView
        if (upcomingEvents.isEmpty()) {
            eventsRecyclerView.setVisibility(View.GONE);
            moreEventsTextView.setVisibility(View.GONE);
            return;
        }

        eventsRecyclerView.setVisibility(View.VISIBLE);

        // Show limited number of events
        List<Event> visibleEvents;
        if (upcomingEvents.size() > MAX_VISIBLE_EVENTS) {
            visibleEvents = upcomingEvents.subList(0, MAX_VISIBLE_EVENTS);
            moreEventsTextView.setVisibility(View.VISIBLE);
            moreEventsTextView.setText("+ " + (upcomingEvents.size() - MAX_VISIBLE_EVENTS) + " więcej wydarzeń");
        } else {
            visibleEvents = upcomingEvents;
            moreEventsTextView.setVisibility(View.GONE);
        }

        eventAdapter.updateEvents(visibleEvents);

        // Update background color based on events within 24 hours
        boolean hasEventWithin24Hours = upcomingEvents.stream().anyMatch(event -> {
            LocalDateTime eventDateTime = LocalDateTime.of(
                    event.getDate(),
                    Optional.ofNullable(event.getTime()).orElse(LocalTime.MIDNIGHT)
            );
            return eventDateTime.isBefore(LocalDateTime.now().plusHours(24));
        });


        int color = hasEventWithin24Hours ? Color.BLACK : Color.WHITE;
        moreEventsTextView.setTextColor(color);

        setColor(hasEventWithin24Hours);

        setBackgroundColor(hasEventWithin24Hours);
    }

    private void setBackgroundColor(boolean hasEventWithin24Hours) {
        if (hasEventWithin24Hours) {
            backgroundLayout.setBackgroundColor(Color.WHITE);
            clockTextView.setTextColor(Color.BLACK);
            dateTextView.setTextColor(Color.BLACK);
            moreEventsTextView.setTextColor(Color.BLACK);
            findViewById(R.id.divider).setBackgroundColor(Color.BLACK);
        } else {
            backgroundLayout.setBackgroundColor(Color.BLACK);
            clockTextView.setTextColor(Color.WHITE);
            dateTextView.setTextColor(Color.WHITE);
            moreEventsTextView.setTextColor(Color.WHITE);
            findViewById(R.id.divider).setBackgroundColor(Color.WHITE);
        }
    }

    private List<Event> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        var now = today.toEpochDay();
        var nowPlus72Hours = today.plusDays(3).toEpochDay();
        Log.d("recurring", "querying for " + nowPlus72Hours);
        return eventDao.getEventsWithin72Hours(nowPlus72Hours, now);
    }

    public void setColor(Boolean hasEventWithin24Hours) {
        Log.d("recurring", "sending event of set color with " + hasEventWithin24Hours);
        int color = hasEventWithin24Hours ? Color.BLACK : Color.WHITE;
        listener.setColor(color);
    }
}