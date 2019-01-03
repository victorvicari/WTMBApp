package com.wtmberlin.data

import androidx.room.*
import io.reactivex.Flowable
import org.threeten.bp.LocalDateTime

class Repository(private val apiService: MeetupService, private val database: EventDatabase) {
    fun events(): Flowable<Result<List<WtmEvent>>> {
        return Flowable.merge(eventsFromDatabase(), eventsFromNetwork())
            .map { Result.success(it) }
            .onErrorReturn { Result.error(it) }
    }

    fun eventsFromNetwork():Flowable<List<WtmEvent>>{
        return apiService.events()
            .map { it.map(MeetupEvent::toWmtEvent) }
            .doOnSuccess {
                database.runInTransaction {
                    database.wtmEventDAO().clear()
                    database.wtmEventDAO().insertAll(it)
                }
            }
            .toFlowable()
    }

    fun eventsFromDatabase():Flowable<List<WtmEvent>>{
        return database.wtmEventDAO().getAll()
    }

    fun group(): Flowable<Result<WtmGroup>> {
        return apiService.group()
            .map { it.toWmtGroup() }
            .map { Result.success(it) }
            .onErrorReturn { Result.error(it) }
            .toFlowable()
    }
}

data class WtmGroup(
    val pastEventCount: Int,
    val members: Int)

@Entity
data class WtmEvent(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "username") val name: String,
    @ColumnInfo(name = "date") val localDateTime: LocalDateTime,
    @ColumnInfo(name = "venue") val venueName: String?)

@Dao
interface WtmEventDAO{
    @Query("SELECT * FROM WtmEvent")
    fun getAll(): Flowable<List<WtmEvent>>

    @Query("SELECT * FROM WtmEvent WHERE id IN (:wtmEventId)")
    fun loadById(wtmEventId: Int): Flowable<WtmEvent>

    @Query("DELETE FROM WtmEvent")
    fun clear()

    @Insert
    fun insertAll(wtmEvents: List<WtmEvent>)
}

private fun MeetupEvent.toWmtEvent() = WtmEvent(
    id = id,
    name = name,
    localDateTime = LocalDateTime.of(local_date, local_time),
    venueName = venue?.name)

private fun MeetupGroup.toWmtGroup() = WtmGroup(
    pastEventCount = past_event_count,
    members = members)



