package com.badoo.reaktive.samplemppmodule.store

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.samplemppmodule.SingleLifeEvent
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
        }

    private fun reload(): Disposable? =
        if (state.isLoading) {
            null
        } else {
            onResult(Result.LoadingStarted)

            loader
                .load()
                .observeOn(computationScheduler)
                .map {
                    when (it) {
                        is KittenLoader.Result.Success -> Result.Loaded(parseUrl(it.json))
                        is KittenLoader.Result.Error -> Result.LoadingFailed
                    }
                }
                .observeOn(mainScheduler)
                .subscribe(onSuccess = ::onResult)
        }

    private fun onResult(result: Result) {
        _states.onNext(Reducer(result, _states.value))
    }

    private companion object {
        private val parseRegex = "(?:\"url\":\")(.*?)(?:\")".toRegex()

        private fun parseUrl(json: String): String = parseRegex.find(json)!!.groupValues[1]
    }

    private sealed class Result {
        object LoadingStarted : Result()
        class Loaded(val kittenUrl: String?) : Result()
        object LoadingFailed : Result()
    }

    private object Reducer {
        operator fun invoke(result: Result, state: State): State =
            when (result) {
                is Result.LoadingStarted -> state.copy(isLoading = true, error = null, kittenUrl = null)
                is Result.Loaded -> state.copy(isLoading = false, error = null, kittenUrl = result.kittenUrl)
                is Result.LoadingFailed -> state.copy(isLoading = false, error = SingleLifeEvent(Unit))
            }
    }
}