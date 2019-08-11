package com.badoo.reaktive.samplemppmodule.binder

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.samplemppmodule.store.KittenStoreBuilder
import com.badoo.reaktive.samplemppmodule.view.KittenView
import com.badoo.reaktive.utils.atomic.AtomicReference

class KittenBinder(
    storeBuilder: KittenStoreBuilder
) {

    private var disposables = CompositeDisposable()
    private val store = storeBuilder.build()
    private var view = AtomicReference<KittenView?>(null, true)

    fun onViewCreated(view: KittenView) {
        this.view.value = view
    }

    fun onStart() {
        disposables +=
            view
                .value!!
                .events
                .map(KittenViewEventToIntentMapper::invoke)
                .subscribe(onNext = store::accept)

        disposables +=
            store
                .states
                .map(KittenStateToViewModelMapper::invoke)
                .subscribe(onNext = { view.value!!.show(it) })
    }

    fun onStop() {
        disposables.clear()
    }

    fun onViewDestroyed() {
        view.value = null
    }

    fun onDestroy() {
        store.dispose()
    }
}