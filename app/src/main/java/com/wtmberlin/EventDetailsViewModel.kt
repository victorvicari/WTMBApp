package com.wtmberlin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wtmberlin.data.*
import com.wtmberlin.util.Event
import com.wtmberlin.util.exhaustive
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class EventDetailsViewModel(private val eventId: String, private val repository: Repository): ViewModel() {
    val event = MutableLiveData<DetailedWtmEvent>()

    val addToCalendar = MutableLiveData<AddToCalendarEvent>()
    val openMaps = MutableLiveData<OpenMapsEvent>()

    private val subscriptions = CompositeDisposable()

    init {
        subscriptions.add(repository.event(eventId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onDataLoaded))
    }

    private fun onDataLoaded(result: Result<DetailedWtmEvent>) {
        when (result) {
            is Result.Success<DetailedWtmEvent> -> event.value = result.data
            is Result.Error<*> -> Timber.w(result.exception)
        }.exhaustive
    }

    fun onDateTimeClicked() {
        event.value?.let {
            addToCalendar.value = AddToCalendarEvent(it.toCalendarEvent())
        }
    }

    fun onLocationClicked() {
        event.value?.let {
            if (it.venueName != null && it.venueCoordinates != null) {
                openMaps.value = OpenMapsEvent(it.venueName, it.venueCoordinates)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        subscriptions.clear()
    }
}

data class OpenMapsEvent(val venueName: String, val coordinates: Coordinates): Event()

data class AddToCalendarEvent(val calendarEvent: CalendarEvent): Event()

data class CalendarEvent(
    val beginTime: Long,
    val endTime: Long,
    val title: String,
    val location: String?)

private fun DetailedWtmEvent.toCalendarEvent() = CalendarEvent(
    beginTime = timeStart,
    endTime = timeStart + duration.toMillis(),
    title = name,
    location = venueName)