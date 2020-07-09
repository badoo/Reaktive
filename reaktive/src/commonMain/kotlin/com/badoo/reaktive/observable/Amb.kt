package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.atomic.AtomicBoolean

fun <T> Iterable<Observable<T>>.amb(): Observable<T> =
    observable { emitter ->
        val sources = toList()

        if (sources.isEmpty()) {
            emitter.onComplete()
            return@observable
        }

        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val hasWinner = AtomicBoolean()

        sources.forEach {
            it.subscribe(AmbObserver(disposables, hasWinner, emitter))
        }
    }

fun <T> amb(vararg sources: Observable<T>): Observable<T> = sources.asList().amb()

private class AmbObserver<in T>(
    private val disposables: CompositeDisposable,
    private val hasWinner: AtomicBoolean,
    private val emitter: ObservableEmitter<T>
) : ObjectReference<Disposable?>(null), ObservableObserver<T> {
    override fun onSubscribe(disposable: Disposable) {
        this.value = disposable
        disposables += disposable
    }

    override fun onNext(value: T) {
        race { emitter.onNext(value) }
    }

    override fun onComplete() {
        race(emitter::onComplete)
    }

    override fun onError(error: Throwable) {
        race { emitter.onError(error) }
    }

    private inline fun race(block: () -> Unit) {
        val disposable: Disposable? = this.value
        if (disposable == null) {
            // This Observable is already a winner
            block()
        } else if (hasWinner.compareAndSet(false, true)) {
            // Only one Observable can win the race
            this.value = null
            disposables -= disposable
            disposables.dispose()
            emitter.setDisposable(disposable)
            block()
        }
    }
}
