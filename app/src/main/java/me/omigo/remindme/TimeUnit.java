package me.omigo.remindme;

import java.util.Arrays;

public enum TimeUnit {
    DAY("Dzień"),
    WEEK("Tydzień"),
    MONTH("Miesiąc"),
    YEAR("Rok");

    private final String label;

    TimeUnit(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static TimeUnit fromLabel(String label) {
        return Arrays.stream(TimeUnit.values())
                .filter(timeUnit -> timeUnit.getLabel().equals(label))
                .findFirst()
                .get();
    }
}
