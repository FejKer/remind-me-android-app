package me.omigo.remindme.events;

import android.util.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RecurringEventCalculator {
    public static List<Event> generateRecurringEventInstances(Event parentEvent, LocalDate startDate, LocalDate endDate) {
        Log.d("recurring", "checking with start " + startDate + " end " + endDate);

        if (!parentEvent.getRecurring()) {
            return Collections.emptyList();
        }

        List<Event> instances = new ArrayList<>();
        LocalTime time = parentEvent.getTime();
        LocalDateTime currentDateTime = LocalDateTime.of(parentEvent.getDate(),
                Optional.ofNullable(time).orElse(LocalTime.of(0, 0, 0)));
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        while (currentDateTime.isBefore(endDateTime)) {
            Log.d("recurring", "current datetime " + currentDateTime);
            if (currentDateTime.toLocalDate().isAfter(startDate) || currentDateTime.toLocalDate().isEqual(startDate)) {
                Event instance = new Event(parentEvent);
                instance.setDate(currentDateTime.toLocalDate());
                instance.setTime(time);
                instance.setParentEventId(parentEvent.getId());
                instance.setRecurring(false);
                instance.setRecurringTimeUnit(parentEvent.getRecurringTimeUnit());
                instance.setRecurringValue(parentEvent.getRecurringValue());
                instance.setHiddenFromScreenSaver(parentEvent.getHiddenFromScreenSaver());
                instances.add(instance);
                Log.d("recurring", "adding");
            }

            var function = parentEvent.getRecurringTimeUnit().getFunction();
            currentDateTime = function.apply(parentEvent.getRecurringValue(), currentDateTime);
        }

        return instances;
    }
}
