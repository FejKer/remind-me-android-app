package me.omigo.remindme.events;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {
    @Insert
    long insert(Event event);

    @Query("SELECT * FROM events ORDER BY date ASC, time ASC")
    List<Event> getAllEvents();

    @Query("SELECT * FROM events WHERE date <=:nowPlus72Hours ORDER BY date ASC, time ASC")
    List<Event> getEventsWithin72Hours(Long nowPlus72Hours);

    @Query("DELETE FROM events")
    void purgeDb();

    @Query("SELECT * FROM events WHERE date =:now AND isRecurring IS FALSE ORDER BY date ASC, time ASC")
    List<Event> getEventsByDateAndNotRecurring(Long now);

    @Update
    void update(Event event);

    @Query("DELETE FROM events WHERE parentEventId =:parentId")
    void deleteByParentId(long parentId);

    @Query("SELECT * FROM events WHERE id =:parentEventId")
    Event findById(Long parentEventId);

    @Query("SELECT * FROM events WHERE isRecurring IS TRUE")
    List<Event> getAllRecurringEvents();
}
