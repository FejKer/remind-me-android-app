package me.omigo.remindme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends BaseAdapter {
    private final Context context;
    private final List<Calendar> dates;
    private final Calendar currentMonth;
    private final List<CalendarAndIsImportantWrapper> markedDates;
    private Calendar selectedDate;
    final OnDateClickListener dateClickListener;

    public CalendarAdapter(Context context, Calendar currentMonth, List<CalendarAndIsImportantWrapper> markedDates, OnDateClickListener listener) {
        this.context = context;
        this.currentMonth = currentMonth;
        this.markedDates = markedDates;
        this.dateClickListener = listener;
        this.selectedDate = null;
        dates = new ArrayList<>();
        populateDates();
    }

    private void populateDates() {
        dates.clear();
        Calendar calendar = (Calendar) currentMonth.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7;
        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek);

        for (int i = 0; i < 42; i++) {
            dates.add((Calendar) calendar.clone());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.calendar_day_cell, parent, false);
        }

        TextView dayText = convertView.findViewById(R.id.dayText);
        View eventIndicator = convertView.findViewById(R.id.eventIndicator);
        View eventIndicatorNotImportant = convertView.findViewById(R.id.eventIndicatorNotImportant);
        Calendar date = dates.get(position);

        dayText.setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));

        // Reset styles
        dayText.setAlpha(1.0f);
        eventIndicator.setVisibility(View.GONE);
        convertView.setBackgroundResource(android.R.color.transparent);

        // Style dates outside current month
        if (date.get(Calendar.MONTH) != currentMonth.get(Calendar.MONTH)) {
            dayText.setAlpha(0.3f);
        }

        // Show indicator for marked dates
        for (var markedDate : markedDates) {
            if (isSameDate(date, markedDate.getCalendar())) {
                Animation pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse);
                if (markedDate.getImportant()) {
                    eventIndicator.setVisibility(View.VISIBLE);
                    eventIndicator.startAnimation(pulseAnimation);
                    eventIndicatorNotImportant.setVisibility(View.GONE);
                    break;
                } else {
                    eventIndicatorNotImportant.setVisibility(View.VISIBLE);
                    eventIndicatorNotImportant.startAnimation(pulseAnimation);
                }
            }
        }

        // Handle selected date
        if (selectedDate != null && isSameDate(date, selectedDate)) {
            convertView.setBackgroundResource(R.drawable.selected_day_background);
        }

        convertView.setOnClickListener(v -> {
            selectedDate = date;
            notifyDataSetChanged();
            if (dateClickListener != null) {
                dateClickListener.onDateClick(date);
            }
        });

        return convertView;
    }

    private boolean isSameDate(Calendar date1, Calendar date2) {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH) &&
                date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH);
    }

    public interface OnDateClickListener {
        void onDateClick(Calendar date);
    }
}