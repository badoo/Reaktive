package com.badoo.reaktive.samplemppmodule.view

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.samplemppmodule.SingleLifeEvent

interface KittenView {

    val events: Observable<Event>

    fun show(model: ViewModel)

    data class ViewModel(
        val isLoading: Boolean,
        val error: SingleLifeEvent<Unit>?,
        val kittenUrl: String?
    )

    sealed class Event {
        object Reload : Event()
    }
}