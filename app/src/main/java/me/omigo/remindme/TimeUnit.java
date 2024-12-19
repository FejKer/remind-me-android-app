package me.omigo.remindme;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.BiFunction;

public enum TimeUnit {
    DAY("Dzień", (interval, localDateTime) -> localDateTime.plusDays(interval)),
    WEEK("Tydzień", (interval, localDateTime) -> localDateTime.plusWeeks(interval)),
    MONTH("Miesiąc", (interval, localDateTime) -> localDateTime.plusMonths(interval)),
    YEAR("Rok", (interval, localDateTime) -> localDateTime.plusYears(interval));

    private final String label;
    private final BiFunction<Integer, LocalDateTime, LocalDateTime> function;

    TimeUnit(String label, BiFunction<Integer, LocalDateTime, LocalDateTime> function) {
        this.label = label;
        this.function = function;
    }

    public String getLabel() {
        return label;
    }

    public BiFunction<Integer, LocalDateTime, LocalDateTime> getFunction() {
        return function;
    }

    public static TimeUnit fromLabel(String label) {
        return Arrays.stream(TimeUnit.values())
                .filter(timeUnit -> timeUnit.getLabel().equals(label))
                .findFirst()
                .get();
    }
}
