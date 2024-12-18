package me.omigo.remindme;

import java.time.DayOfWeek;
import java.util.Arrays;

public enum PolishDayOfWeek {
    PONIEDZIALEK(DayOfWeek.MONDAY, "Poniedziałek"),
    WTOREK(DayOfWeek.TUESDAY, "Wtorek"),
    SRODA(DayOfWeek.WEDNESDAY, "Środa"),
    CZWARTEK(DayOfWeek.THURSDAY, "Czwartek"),
    PIATEK(DayOfWeek.FRIDAY, "Piątek"),
    SOBOTA(DayOfWeek.SATURDAY, "Sobota"),
    NIEDZIELA(DayOfWeek.SUNDAY, "Niedziela");

    private final DayOfWeek dayOfWeek;
    private final String displayName;

    PolishDayOfWeek(DayOfWeek dayOfWeek, String displayName) {
        this.dayOfWeek = dayOfWeek;
        this.displayName = displayName;
    }

    public DayOfWeek toDayOfWeek() {
        return dayOfWeek;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PolishDayOfWeek fromLabel(String label) {
        return Arrays.stream(PolishDayOfWeek.values())
                .filter(d -> d.getDisplayName().equals(label))
                .findFirst()
                .get();
    }

    public static PolishDayOfWeek fromDayOfWeek(DayOfWeek dayOfWeek) {
        for (PolishDayOfWeek day : values()) {
            if (day.dayOfWeek == dayOfWeek) {
                return day;
            }
        }
        throw new IllegalArgumentException("Unknown day of week: " + dayOfWeek);
    }
}
