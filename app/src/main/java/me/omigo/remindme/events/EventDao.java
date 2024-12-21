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

    @Query("SELECT * FROM events WHERE deleted IS FALSE ORDER BY date ASC, time ASC")
    List<Event> getAllEvents();

    @Query("SELECT * FROM events WHERE date <=:nowPlus72Hours AND date >=:today AND deleted IS FALSE AND isHiddenFromScreenSaver IS FALSE ORDER BY date ASC, time ASC")
    List<Event> getEventsWithin72Hours(Long nowPlus72Hours, Long today);

    @Query("DELETE FROM events")
    void purgeDb();

    @Query("SELECT * FROM events WHERE date =:now AND isRecurring IS FALSE AND deleted IS FALSE ORDER BY date ASC, time ASC")
    List<Event> getEventsByDateAndNotRecurring(Long now);

    @Update
    void update(Event event);

    @Query("DELETE FROM events WHERE parentEventId =:parentId")
    void deleteByParentId(long parentId);

    @Query("SELECT * FROM events WHERE id =:parentEventId")
    Event findById(Long parentEventId);

    @Query("SELECT * FROM events WHERE isRecurring IS TRUE AND deleted IS FALSE")
    List<Event> getAllRecurringEvents();

    @Query("UPDATE events SET deleted = TRUE WHERE id =:id")
    void delete(Long id);

    @Query("SELECT title FROM events WHERE title LIKE :s")
    List<String> getSimilarTitles(String s);

    @Query("SELECT place FROM events WHERE place LIKE :s")
    List<String> getSimilarPlaces(String s);
}
