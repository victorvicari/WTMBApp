package com.wtmberlin.data

import io.reactivex.Flowable
import org.threeten.bp.LocalDateTime

class Repository(private val apiService: MeetupService) {
    fun events(): Flowable<Result<List<WtmEvent>>> {
        return apiService.events()
            .map { it.map(MeetupEvent::toWmtEvent) }
            .map { Result.success(it) }
            .onErrorReturn { Result.error(it) }
            .toFlowable()
    }
}

data class WtmEvent(
    val id: String,
    val name: String,
    val localDateTime: LocalDateTime,
    val venueName: String?)

fun MeetupEvent.toWmtEvent() = WtmEvent(
    id = id,
    name = name,
    localDateTime = LocalDateTime.of(local_date, local_time),
    venueName = venue?.name)

