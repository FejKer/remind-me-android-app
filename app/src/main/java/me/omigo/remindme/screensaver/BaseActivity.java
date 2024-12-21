package me.omigo.remindme.screensaver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    private static final long INACTIVE_TIMEOUT = 30000;
    private Handler inactivityHandler = new Handler();

    private final Runnable inactivityRunnable = this::startScreenSaver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupInactivityTimer();
    }

    private void setupInactivityTimer() {
        Log.d("recurring", "resetting timer");
        View decorView = getWindow().getDecorView();
        decorView.setOnTouchListener((v, event) -> {
            resetInactivityTimer();
            return false;
        });
    }

    protected void resetInactivityTimer() {
        inactivityHandler.removeCallbacks(inactivityRunnable);
        inactivityHandler.postDelayed(inactivityRunnable, INACTIVE_TIMEOUT);
    }

    private void startScreenSaver() {
        Log.d("recurring", "Putting into screen saver mode");
        Intent intent = new Intent(this, EventScreenSaverActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        resetInactivityTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        inactivityHandler.removeCallbacks(inactivityRunnable);
    }

    // Override dispatchTouchEvent to catch all touch events
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d("recurring", "resetting timer");
        resetInactivityTimer();
        return super.dispatchTouchEvent(ev);
    }
}