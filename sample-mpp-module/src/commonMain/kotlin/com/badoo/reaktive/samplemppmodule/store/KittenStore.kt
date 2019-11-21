package com.badoo.reaktive.samplemppmodule.store

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable

interface KittenStore : Disposable {

    val states: Observable<State>

    fun accept(intent: Intent)

    data class State(
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val kittenUrl: String? = null
    )

    sealed class Intent {
        object Reload : Intent()
        object DismissError : Intent()
    }
}
