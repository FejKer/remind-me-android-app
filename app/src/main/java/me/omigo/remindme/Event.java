package me.omigo.remindme;

import androidx.room.Embedded;
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

    @Embedded
    private RecurrencePattern recurrencePattern;

    private Long parentEventId; // For recurring event instances

    public Event(String title, String place, LocalDate date, LocalTime time, Priority priority) {
        this.title = title;
        this.place = place;
        this.date = date;
        this.time = time;
        this.priority = priority;
    }

    public Event(long id, String title, String place, LocalDate date, LocalTime time, Priority priority, RecurrencePattern recurrencePattern, Long parentEventId) {
        this.id = id;
        this.title = title;
        this.place = place;
        this.date = date;
        this.time = time;
        this.priority = priority;
        this.recurrencePattern = recurrencePattern;
        this.parentEventId = parentEventId;
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

    public RecurrencePattern getRecurrencePattern() {
        return recurrencePattern;
    }

    public void setRecurrencePattern(RecurrencePattern recurrencePattern) {
        this.recurrencePattern = recurrencePattern;
    }

    public Long getParentEventId() {
        return parentEventId;
    }

    public void setParentEventId(Long parentEventId) {
        this.parentEventId = parentEventId;
    }
}
