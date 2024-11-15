package me.omigo.remindme;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.time.LocalDate;
import java.util.List;

@Dao
public interface EventDao {
    @Insert
    void insert(Event event);

    @Query("SELECT * FROM events")
    List<Event> getAllEvents();

    @Query("DELETE FROM events")
    void purgeDb();

    @Query("SELECT * FROM events WHERE date =:now")
    List<Event> getEventsByDate(Long now);
}
