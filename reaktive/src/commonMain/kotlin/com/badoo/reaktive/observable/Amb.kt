package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.minusAssign
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.ObjectReference

fun <T> Iterable<Observable<T>>.amb(): Observable<T> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val markerDisposable = Disposable()
        disposables += markerDisposable

        forEach {
            it.subscribe(AmbObserver(disposables, markerDisposable, emitter))
        }
    }

fun <T> amb(vararg sources: Observable<T>): Observable<T> = sources.toList().amb()

private class AmbObserver<in T>(
    private val disposables: CompositeDisposable,
    private val markerDisposable: Disposable,
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
        } else if (disposables.remove(markerDisposable)) {
            // Only one Observable can win the race because CompositeDisposable.remove is atomic
            this.value = null
            disposables -= disposable
            disposables.dispose()
            emitter.setDisposable(disposable)
            block()
        }
    }
}
