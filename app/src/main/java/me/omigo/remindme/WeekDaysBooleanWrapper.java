package me.omigo.remindme;

import java.util.Objects;

public class WeekDaysBooleanWrapper {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeekDaysBooleanWrapper wrapper = (WeekDaysBooleanWrapper) o;
        return dayOfWeek == wrapper.dayOfWeek && Objects.equals(active, wrapper.active);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOfWeek, active);
    }

    private PolishDayOfWeek dayOfWeek;
    private Boolean active = Boolean.FALSE;

    public PolishDayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(PolishDayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String toDatabaseValue() {
        return this.dayOfWeek.getDisplayName() + "," + this.active.toString();
    }
}
