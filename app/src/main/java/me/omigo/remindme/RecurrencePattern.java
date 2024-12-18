package me.omigo.remindme;


import java.time.LocalDate;
import java.util.List;

public class RecurrencePattern {
    @Override
    public String toString() {
        return "RecurrencePattern{" +
                "recurrenceEnabled=" + recurrenceEnabled +
                ", type=" + type +
                ", interval=" + interval +
                ", timeUnit=" + timeUnit +
                ", weekDays=" + weekDays +
                ", endDate=" + endDate +
                '}';
    }

    private Boolean recurrenceEnabled;
    private RecurrenceType type;
    private Integer interval; // For custom intervals (every X days/weeks/months/years)
    private TimeUnit timeUnit; // "day", "week", "month", "year"
    private List<WeekDaysBooleanWrapper> weekDays; // For weekly recurrence [Sun,Mon,Tue,Wed,Thu,Fri,Sat]
    private LocalDate endDate; // Optional end date for recurrence

    public Boolean getRecurrenceEnabled() {
        return recurrenceEnabled;
    }

    public void setRecurrenceEnabled(Boolean recurrenceEnabled) {
        this.recurrenceEnabled = recurrenceEnabled;
    }

    public RecurrenceType getType() {
        return type;
    }

    public void setType(RecurrenceType type) {
        this.type = type;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public List<WeekDaysBooleanWrapper> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(List<WeekDaysBooleanWrapper> weekDays) {
        this.weekDays = weekDays;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
