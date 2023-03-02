package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.addTo
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.queue.SharedQueue

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Observable].
 * Emits elements from inner [Observable]s. The maximum number of concurrently subscribed inner [Observable]s is
 * determined by the [maxConcurrency] argument.
 *
 * By default, all inner [Observable]s are subscribed concurrently without any limits.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#flatMap-io.reactivex.functions.Function-int-).
 */
fun <T, R> Observable<T>.flatMap(maxConcurrency: Int = Int.MAX_VALUE, mapper: (T) -> Observable<R>): Observable<R> {
    require(maxConcurrency > 0) { "maxConcurrency value must be positive" }

    return observable { emitter ->
        val upstreamObserver = FlatMapObserver(emitter.serialize(), maxConcurrency, mapper)
        emitter.setDisposable(upstreamObserver)
        subscribe(upstreamObserver)
    }
}

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Observable].
 * For each element [U] emitted by an inner [Observable], calls [resultSelector] with the original source element [T]
 * and the inner element [U], and emits the result element [R]. The maximum number of concurrently subscribed inner [Observable]s is
 * determined by the [maxConcurrency] argument.
 *
 * By default, all inner [Observable]s are subscribed concurrently without any limits.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#flatMap-io.reactivex.functions.Function-io.reactivex.functions.BiFunction-int-).
 */
fun <T, U, R> Observable<T>.flatMap(
    resultSelector: (T, U) -> R,
    maxConcurrency: Int = Int.MAX_VALUE,
    mapper: (T) -> Observable<U>
): Observable<R> =
    flatMap(maxConcurrency = maxConcurrency) { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }

private class FlatMapObserver<in T, in R>(
    private val callbacks: ObservableCallbacks<R>,
    maxConcurrency: Int,
    private val mapper: (T) -> Observable<R>
) : CompositeDisposable(), ObservableObserver<T>, ErrorCallback by callbacks {

    private val activeSourceCount = AtomicInt(1)

    private val queue: FlatMapQueue<Observable<R>>? =
        maxConcurrency
            .takeIf { it < Int.MAX_VALUE }
            ?.let { FlatMapQueue(limit = it, callback = ::subscribeInner) }
            ?.addTo(this)

    override fun onSubscribe(disposable: Disposable) {
        add(disposable)
    }

    override fun onNext(value: T) {
        activeSourceCount.addAndGet(1)

        callbacks.tryCatch({ mapper(value) }) { inner ->
            if (queue == null) {
                subscribeInner(inner)
            } else {
                queue.offer(inner)
            }
        }
    }

    private fun subscribeInner(inner: Observable<R>) {
        callbacks.tryCatch {
            inner.subscribe(InnerObserver())
        }
    }

    override fun onComplete() {
        if (activeSourceCount.addAndGet(-1) <= 0) {
            callbacks.onComplete()
        }
    }

    private inner class InnerObserver :
        ObservableObserver<R>,
        ErrorCallback by callbacks,
        ValueCallback<R> by callbacks {

        private var disposableRef: Disposable? = null

        override fun onSubscribe(disposable: Disposable) {
            disposableRef = disposable
            add(disposable)
        }

        override fun onComplete() {
            remove(requireNotNull(disposableRef))
            queue?.poll()
            this@FlatMapObserver.onComplete()
        }
    }
}

private class FlatMapQueue<in T : Any>(
    limit: Int,
    private val callback: (T) -> Unit
) : Disposable {

    private val lock = Lock()
    private val count = AtomicInt(limit)
    private val queue = SharedQueue<T>()

    private val _isDisposed = AtomicBoolean(false)
    override val isDisposed: Boolean get() = _isDisposed.value

    override fun dispose() {
        lock.synchronized {
            _isDisposed.value = true
            count.value = 0
            queue.clear()
        }
    }

    fun offer(value: T) {
        sync {
            if (count.value > 0) {
                count.value--
                value
            } else {
                queue.offer(value)
                null
            }
        }?.also(callback)
    }

    fun poll() {
        sync {
            val next = queue.poll()
            if (next == null) {
                count.value++
            }
            next
        }?.also(callback)
    }

    private inline fun <T> sync(block: () -> T): T? =
        lock.synchronized {
            if (_isDisposed.value) null else block()
        }
}
