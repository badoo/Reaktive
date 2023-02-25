package com.badoo.reaktive.samplemppmodule

import com.badoo.reaktive.base.invoke
import com.badoo.reaktive.disposable.scope.DisposableScope
import com.badoo.reaktive.observable.BehaviorObservableWrapper
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.observableFromFunction
import com.badoo.reaktive.observable.observableOf
import com.badoo.reaktive.observable.observableOfEmpty
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.startWithValue
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.observable.wrap
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.mainScheduler

class Counter : DisposableScope by DisposableScope() {

    private val feature =
        Feature(initialState = State(), actor = ::onEvent) { msg ->
            when (msg) {
                is Msg.Set -> copy(value = msg.value, isLoading = false)
                is Msg.LoadingStarted -> copy(isLoading = true)
            }
        }.scope()

    val state: BehaviorObservableWrapper<State> = feature.state.wrap()

    private fun onEvent(event: Event, state: State): Observable<Msg> {
        if (state.isLoading) {
            return observableOfEmpty()
        }

        return when (event) {
            is Event.Increment -> observableOf(Msg.Set(value = state.value + 1L))
            is Event.Decrement -> observableOf(Msg.Set(value = state.value - 1L))
            is Event.Reset -> observableOf(Msg.Set(value = 0L))
            is Event.Fibonacci -> fibonacci(n = state.value)
        }
    }

    private fun fibonacci(n: Long): Observable<Msg> =
        observableFromFunction { calcFibonacci(n = n) }
            .subscribeOn(computationScheduler)
            .map(Msg::Set)
            .observeOn(mainScheduler)
            .startWithValue(Msg.LoadingStarted)

    private fun calcFibonacci(n: Long): Long =
        when {
            n <= 0L -> 0L
            n == 1L -> 1L
            else -> calcFibonacci(n = n - 2L) + calcFibonacci(n = n - 1L) // Intentionally suboptimal long-running task
        }

    fun onEvent(event: Event) {
        feature(event)
    }

    data class State(
        val value: Long = 0L,
        val isLoading: Boolean = false,
    )

    sealed interface Event {
        object Increment : Event
        object Decrement : Event
        object Reset : Event
        object Fibonacci : Event
    }

    private sealed interface Msg {
        data class Set(val value: Long) : Msg
        object LoadingStarted : Msg
    }
}
