package com.badoo.reaktive.samplemppmodule.store

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.samplemppmodule.store.KittenStore.Intent
import com.badoo.reaktive.samplemppmodule.store.KittenStore.State
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.single.subscribe
import com.badoo.reaktive.subject.behavior.behaviorSubject

internal class KittenStoreImpl(
    private val loader: KittenLoader
) : KittenStore {

    private val _states = behaviorSubject(State())
    override val states: Observable<State> = _states
    private val state: State get() = _states.value

    private val disposables = CompositeDisposable()
    override val isDisposed: Boolean get() = disposables.isDisposed

    override fun dispose() {
        disposables.dispose()
        _states.onComplete()
    }

    override fun accept(intent: Intent) {
        execute(intent)?.also(disposables::add)
    }

    private fun execute(intent: Intent): Disposable? =
        when (intent) {
            is Intent.Reload -> reload()

            is Intent.DismissError -> {
                onResult(Effect.DismissErrorRequested)
                null
            }
        }

    private fun reload(): Disposable? =
        if (state.isLoading) {
            null
        } else {
            onResult(Effect.LoadingStarted)

            loader
                .load()
                .observeOn(computationScheduler)
                .map {
                    when (it) {
                        is KittenLoader.Result.Success -> Effect.Loaded(parseUrl(it.json))
                        is KittenLoader.Result.Error -> Effect.LoadingFailed
                    }
                }
                .observeOn(mainScheduler)
                .subscribe(isThreadLocal = true, onSuccess = ::onResult)
        }

    private fun onResult(effect: Effect) {
        _states.onNext(Reducer(effect, _states.value))
    }

    private companion object {
        private val parseRegex = "(?:\"url\":\")(.*?)(?:\")".toRegex()

        private fun parseUrl(json: String): String = parseRegex.find(json)!!.groupValues[1]
    }

    private sealed class Effect {
        object LoadingStarted : Effect()
        class Loaded(val kittenUrl: String?) : Effect()
        object LoadingFailed : Effect()
        object DismissErrorRequested : Effect()
    }

    private object Reducer {
        operator fun invoke(effect: Effect, state: State): State =
            when (effect) {
                is Effect.LoadingStarted -> state.copy(isLoading = true, isError = false, kittenUrl = null)
                is Effect.Loaded -> state.copy(isLoading = false, isError = false, kittenUrl = effect.kittenUrl)
                is Effect.LoadingFailed -> state.copy(isLoading = false, isError = true)
                is Effect.DismissErrorRequested -> state.copy(isError = false)
            }
    }
}