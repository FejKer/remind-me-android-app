package me.omigo.remindme;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Converters {

    @TypeConverter
    public static List<WeekDaysBooleanWrapper> fromStringToListOfWeekDays(String value) {
        if (value == null) {
            return null;
        }
        String[] split = value.split(";");
        List<WeekDaysBooleanWrapper> result = new ArrayList<>();
        for (var s : split) {
            String[] object = s.split(",");
            PolishDayOfWeek polishDayOfWeek = PolishDayOfWeek.fromLabel(object[0]);
            Boolean active = Boolean.parseBoolean(object[1]);
            WeekDaysBooleanWrapper wrapper = new WeekDaysBooleanWrapper();
            wrapper.setDayOfWeek(polishDayOfWeek);
            wrapper.setActive(active);
            result.add(wrapper);
        }
        return result;
    }

    @TypeConverter
    public static String fromListOfWeekDaysToString(List<WeekDaysBooleanWrapper> value) {
        if (value == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (var v : value) {
            sb.append(v.toDatabaseValue()).append(";");
        }
        String result = sb.toString();
        if (result.endsWith(";")) {
            return result.substring(0, result.length() - 2);
        }
        return result;
    }

    @TypeConverter
    public static TimeUnit fromStringToTimeUnit(String value) {
        return TimeUnit.from(value);
    }

    @TypeConverter
    public static String fromTimeUnitToString(TimeUnit value) {
        return value.getLabel();
    }

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
