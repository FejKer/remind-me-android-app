package me.omigo.remindme.screensaver;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import me.omigo.remindme.AppDatabase;
import me.omigo.remindme.R;
import me.omigo.remindme.events.Event;
import me.omigo.remindme.events.EventDao;
import me.omigo.remindme.events.RecurringEventCalculator;

public class EventScreenSaverActivity extends BaseActivity {
    private TextView eventsTextView;
    private ConstraintLayout backgroundLayout;
    private Handler updateHandler;
    private static final long UPDATE_INTERVAL = 60000; // Update every minute
    private EventDao eventDao;

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
        eventsTextView = findViewById(R.id.eventsTextView);
        backgroundLayout = findViewById(R.id.backgroundLayout);

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
        List<Event> upcomingEvents = getUpcomingEvents();

        for (var event : eventDao.getAllRecurringEvents()) {
            var recurringEvents = RecurringEventCalculator.generateRecurringEventInstances(event, LocalDate.now(), LocalDate.now().plusDays(3));
            upcomingEvents.addAll(recurringEvents);
        }

        Log.d("recurring", "upcoming " + upcomingEvents);
        if (upcomingEvents.isEmpty()) {
            eventsTextView.setText("Brak wydarzeń w ciągu 3 dni");
            setBackgroundColor(false);
            return;
        }

        boolean hasEventWithin24Hours = false;
        StringBuilder displayText = new StringBuilder();

        for (Event event : upcomingEvents) {
            LocalDateTime localDateTime = LocalDateTime.of(event.getDate(), Optional.ofNullable(event.getTime()).orElse(LocalTime.of(0, 0, 0)));

            if (localDateTime.isBefore(LocalDateTime.now().plusHours(24)) || localDateTime.isEqual(LocalDateTime.now().plusHours(24))) {
                hasEventWithin24Hours = true;
            }

            displayText.append(formatEventDisplay(event)).append("\n\n");
        }

        eventsTextView.setText(displayText.toString().trim());
        setBackgroundColor(hasEventWithin24Hours);
    }

    private void setBackgroundColor(boolean hasEventWithin24Hours) {
        if (hasEventWithin24Hours) {
            backgroundLayout.setBackgroundColor(Color.WHITE);
            eventsTextView.setTextColor(Color.BLACK);
        } else {
            backgroundLayout.setBackgroundColor(Color.BLACK);
            eventsTextView.setTextColor(Color.WHITE);
        }
    }

    private String formatEventDisplay(Event event) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        DateTimeFormatter dateOnlyFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        String title = event.getTitle();
        LocalTime time = event.getTime();
        LocalDate date = event.getDate();

        if (time != null) {
            return title + "\n" + dateFormat.format(LocalDateTime.of(date, time));
        } else {
            return title + "\n" + dateOnlyFormat.format(date) + " całodniowe";
        }
    }

    private List<Event> getUpcomingEvents() {
        LocalDate today = LocalDate.now();
        var now = today.toEpochDay();
        var nowPlus72Hours = today.plusDays(3).toEpochDay();
        Log.d("recurring", "querying for " + nowPlus72Hours);
        return eventDao.getEventsWithin72Hours(nowPlus72Hours, now);
    }
}
