package me.omigo.remindme;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends BaseAdapter {

    private final Context context;
    private final List<Calendar> dates;
    private final Calendar currentMonth;
    private final List<Calendar> markedDates;
    private Calendar selectedDate;

    final OnDateClickListener dateClickListener;

    public CalendarAdapter(Context context, Calendar currentMonth, List<Calendar> markedDates, OnDateClickListener listener) {
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
        // Start at the beginning of the month
        Calendar calendar = (Calendar) currentMonth.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Find the first day to display (including previous month dates)
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Sunday = 1
        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek);

        // Populate the 6 weeks (6 rows * 7 days)
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
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView dayText = (TextView) convertView;
        Calendar date = dates.get(position);

        // Set day number
        dayText.setText(String.valueOf(date.get(Calendar.DAY_OF_MONTH)));

        // Default style
        dayText.setBackgroundColor(Color.TRANSPARENT);
        dayText.setTextColor(Color.BLACK);

        // Highlight current month dates
        if (date.get(Calendar.MONTH) != currentMonth.get(Calendar.MONTH)) {
            dayText.setTextColor(Color.GRAY); // Outside current month
        }

        // Highlight marked dates
        for (Calendar markedDate : markedDates) {
            if (isSameDate(date, markedDate)) {
                dayText.setBackgroundColor(Color.RED); // Highlight background
                dayText.setTextColor(Color.WHITE);     // Change text color
            }
        }

        // Highlight selected date
        if (selectedDate != null && isSameDate(date, selectedDate)) {
            dayText.setBackgroundColor(Color.BLUE); // Selected date background
            dayText.setTextColor(Color.WHITE);      // Selected date text color
        }

        // Handle click events
        dayText.setOnClickListener(v -> {
            selectedDate = date;
            notifyDataSetChanged(); // Refresh the view
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

    // Listener interface for date clicks
    public interface OnDateClickListener {
        void onDateClick(Calendar date);
    }
}