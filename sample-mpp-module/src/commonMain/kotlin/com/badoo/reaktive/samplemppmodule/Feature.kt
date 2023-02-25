package com.badoo.reaktive.samplemppmodule

import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.scope.DisposableScope
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.subject.behavior.BehaviorObservable
import com.badoo.reaktive.subject.behavior.BehaviorSubject
import com.badoo.reaktive.subject.publish.PublishSubject

internal interface Feature<in Wish : Any, out State : Any> : Consumer<Wish>, Disposable {

    val state: BehaviorObservable<State>
}

@Suppress("FunctionNaming") // Factory function
internal fun <Event : Any, State : Any, Msg : Any> Feature(
    initialState: State,
    actor: (Event, State) -> Observable<Msg>,
    reducer: State.(Msg) -> State,
): Feature<Event, State> =
    object : Feature<Event, State>, DisposableScope by DisposableScope() {
        private val wishes = PublishSubject<Event>()
        private val _state = BehaviorSubject(initialState).scope { it.onComplete() }
        override val state: BehaviorObservable<State> = _state

        init {
            wishes
                .flatMap { event -> actor(event, _state.value) }
                .map { msg -> _state.value.reducer(msg) }
                .subscribeScoped(onNext = _state::onNext)
        }

        override fun onNext(value: Event) {
            wishes.onNext(value)
        }
    }
