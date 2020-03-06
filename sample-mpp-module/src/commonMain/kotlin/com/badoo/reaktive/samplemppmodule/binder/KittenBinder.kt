package com.badoo.reaktive.samplemppmodule.binder

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.disposable.scope.DisposableScope
import com.badoo.reaktive.disposable.scope.disposableScope
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.samplemppmodule.store.KittenStoreBuilder
import com.badoo.reaktive.samplemppmodule.view.KittenView

@OptIn(ExperimentalReaktiveApi::class)
class KittenBinder(
    storeBuilder: KittenStoreBuilder
) {

    private var startStopScope: DisposableScope? = null
    private val store = storeBuilder.build()
    private var view: KittenView? = null

    fun onViewCreated(view: KittenView) {
        this.view = view
    }

    fun onStart() {
        startStopScope = disposableScope { start() }
    }

    private fun DisposableScope.start() {
        requireNotNull(view)
            .events
            .map(KittenViewEventToIntentMapper::invoke)
            .subscribeScoped(onNext = store::accept)

        store
            .states
            .map(KittenStateToViewModelMapper::invoke)
            .subscribeScoped(onNext = { requireNotNull(view).show(it) })
    }

    fun onStop() {
        startStopScope?.dispose()
        startStopScope = null
    }

    fun onViewDestroyed() {
        view = null
    }

    fun onDestroy() {
        store.dispose()
    }
}
