package com.badoo.reaktive.samplemppmodule.store

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.samplemppmodule.SingleLifeEvent

interface KittenStore : Disposable {

    val states: Observable<State>

    fun accept(intent: Intent)

    data class State(
        val isLoading: Boolean = false,
        val error: SingleLifeEvent<Unit>? = null,
        val kittenUrl: String? = null
    )

    sealed class Intent {
        object Reload : Intent()
    }
}

