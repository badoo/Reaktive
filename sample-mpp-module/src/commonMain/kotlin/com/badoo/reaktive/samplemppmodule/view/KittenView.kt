package com.badoo.reaktive.samplemppmodule.view

import com.badoo.reaktive.observable.Observable

interface KittenView {

    val events: Observable<Event>

    fun show(model: ViewModel)

    data class ViewModel(
        val isLoading: Boolean,
        val isError: Boolean,
        val kittenUrl: String?
    )

    sealed class Event {
        object Reload : Event()
        object ErrorShown : Event()
    }
}
