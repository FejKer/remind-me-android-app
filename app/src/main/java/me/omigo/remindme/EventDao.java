package me.omigo.remindme;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface EventDao {
    @Insert
    long insert(Event event);

    @Query("SELECT * FROM events ORDER BY date ASC, time ASC")
    List<Event> getAllEvents();

    @Query("DELETE FROM events")
    void purgeDb();

    @Query("SELECT * FROM events WHERE date =:now ORDER BY date ASC, time ASC")
    List<Event> getEventsByDate(Long now);

    @Update
    void update(Event event);

    @Query("SELECT * FROM events WHERE recurrenceEnabled IS TRUE")
    List<Event> getRecurringEvents();

    @Query("SELECT * FROM events WHERE parentEventId = :parentId")
    List<Event> getRecurringInstances(long parentId);
}
