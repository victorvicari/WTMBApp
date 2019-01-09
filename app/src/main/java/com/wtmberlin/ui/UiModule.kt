package com.wtmberlin.ui

import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val uiModule = module {
    viewModel { EventsViewModel(get()) }
    viewModel { (eventId: String) -> EventDetailsViewModel(eventId, get()) }
    viewModel { CollaborationsViewModel(get()) }
}