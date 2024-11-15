package me.omigo.remindme;


import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalTime;

public class Converters {

    @TypeConverter
    public static LocalDate fromTimestampToLocalDate(Long value) {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long fromLocalDateToTimestamp(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }

    @TypeConverter
    public static LocalTime fromTimestampToLocalTime(Integer value) {
        return value == null ? null : LocalTime.ofSecondOfDay(value);
    }

    @TypeConverter
    public static Integer fromLocalTimeToTimestamp(LocalTime time) {
        return time == null ? null : time.toSecondOfDay();
    }
}
