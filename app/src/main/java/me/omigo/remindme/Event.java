package me.omigo.remindme;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {
    private String title;
    private String place;
    private LocalDate date;
    private LocalTime time;

    public Event(String title, String place, LocalDate date, LocalTime time) {
        this.title = title;
        this.place = place;
        this.date = date;
        this.time = time;
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
}
