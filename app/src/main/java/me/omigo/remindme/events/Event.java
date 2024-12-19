package me.omigo.remindme.events;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity(tableName = "events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String place;
    private LocalDate date;
    private LocalTime time;
    private Priority priority;

    private Boolean isRecurring = Boolean.FALSE;
    private Integer recurringValue;
    private TimeUnit recurringTimeUnit;
    private Long parentEventId = null;


    public Event(String title, String place, LocalDate date, LocalTime time, Priority priority) {
        this.title = title;
        this.place = place;
        this.date = date;
        this.time = time;
        this.priority = priority;
    }

    public Event(String title, String place, LocalDate date, LocalTime time, Priority priority, Boolean isRecurring, Integer recurringValue, TimeUnit recurringTimeUnit) {
        this.title = title;
        this.place = place;
        this.date = date;
        this.time = time;
        this.priority = priority;
        this.isRecurring = isRecurring;
        this.recurringValue = recurringValue;
        this.recurringTimeUnit = recurringTimeUnit;
    }

    public Event(Event event) {
        this.title = event.getTitle();
        this.place = event.getPlace();
        this.date = event.getDate();
        this.time = event.getTime();
        this.priority = event.getPriority();
        this.parentEventId = event.getId();
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", place='" + place + '\'' +
                ", date=" + date +
                ", time=" + time +
                ", priority=" + priority +
                ", isRecurring=" + isRecurring +
                ", recurringValue=" + recurringValue +
                ", recurringTimeUnit=" + recurringTimeUnit +
                ", parentEventId=" + parentEventId +
                '}';
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Event() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Boolean getRecurring() {
        return isRecurring;
    }

    public void setRecurring(Boolean recurring) {
        isRecurring = recurring;
    }

    public Integer getRecurringValue() {
        return recurringValue;
    }

    public void setRecurringValue(Integer recurringValue) {
        this.recurringValue = recurringValue;
    }

    public TimeUnit getRecurringTimeUnit() {
        return recurringTimeUnit;
    }

    public void setRecurringTimeUnit(TimeUnit recurringTimeUnit) {
        this.recurringTimeUnit = recurringTimeUnit;
    }

    public Long getParentEventId() {
        return parentEventId;
    }

    public void setParentEventId(long parentEventId) {
        this.parentEventId = parentEventId;
    }
}