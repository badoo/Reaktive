package com.badoo.reaktive.samplemppmodule.store


import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.disposable.scope.DisposableScope
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.samplemppmodule.store.KittenStore.Intent
import com.badoo.reaktive.samplemppmodule.store.KittenStore.State
import com.badoo.reaktive.scheduler.computationScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.observeOn
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.badoo.reaktive.utils.ensureNeverFrozen


@UseExperimental(ExperimentalReaktiveApi::class)
internal class KittenStoreImpl(
    private val loader: KittenLoader
) : KittenStore, DisposableScope by DisposableScope() {

    private val _states = BehaviorSubject(State()).ensureNeverFrozen().scope()
    override val states: Observable<State> = _states
    private val state: State get() = _states.value

    override fun accept(intent: Intent) {
        execute(intent)
    }

    private fun execute(intent: Intent) {
        when (intent) {
            is Intent.Reload -> reload()
            is Intent.DismissError -> onResult(Effect.DismissErrorRequested)
        }.also {}
    }

    private fun reload() {
        if (state.isLoading) {
            return
        }

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
            .subscribeScoped(isThreadLocal = true, onSuccess = ::onResult)
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
