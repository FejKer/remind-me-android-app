package me.omigo.remindme.calendar;

import java.util.Calendar;

public class CalendarAndIsImportantWrapper {
    private Calendar calendar;

    private Boolean isImportant;

    public CalendarAndIsImportantWrapper(Calendar calendar, Boolean isImportant) {
        this.calendar = calendar;
        this.isImportant = isImportant;
    }


    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public Boolean getImportant() {
        return isImportant;
    }

    public void setImportant(Boolean important) {
        isImportant = important;
    }
}
