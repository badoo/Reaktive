package com.badoo.reaktive.observable

import com.badoo.reaktive.base.setCancellable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.subject.unicast.UnicastSubject
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.queue.SharedQueue

/**
 * Please refer to the corresponding RxJava
 * [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#window-long-long-).
 */
fun <T> Observable<T>.window(
    count: Long,
    skip: Long = count
): Observable<Observable<T>> {
    require(count > 0) { "count > 0 required but it was $count" }
    require(skip > 0) { "skip > 0 required but it was $skip" }

    return observable { emitter ->
        val upstreamDisposableWrapper = DisposableWrapper()

        val windows = SharedQueue<UnicastSubject<T>>()
        val activeWindowsCount = AtomicInt(1)

        emitter.setCancellable {
            if (activeWindowsCount.addAndGet(-1) == 0) {
                upstreamDisposableWrapper.dispose()
            }
        }

        val onTerminate: () -> Unit = {
            if (activeWindowsCount.addAndGet(-1) == 0 && emitter.isDisposed) {
                upstreamDisposableWrapper.dispose()
            }
        }

        subscribe(object : ObservableObserver<T> {

            private val skippedCount = AtomicLong()
            private val tailWindowValuesCount = AtomicLong()

            override fun onSubscribe(disposable: Disposable) {
                upstreamDisposableWrapper.set(disposable)
            }

            override fun onNext(value: T) {
                val skipped = skippedCount.value

                val window: UnicastSubject<T>?
                val windowSubscribed: AtomicBoolean?

                if (skipped == 0L) {
                    activeWindowsCount.addAndGet(1)
                    window = UnicastSubject(onTerminate = onTerminate)
                    windowSubscribed = AtomicBoolean(false)
                    windows.offer(window)
                    emitter.onNext(window.doOnAfterSubscribe { windowSubscribed.value = true })
                } else {
                    window = null
                    windowSubscribed = null
                }

                windows.forEach { it.onNext(value) }

                skippedCount.value = (skipped + 1) % skip

                if (tailWindowValuesCount.value + 1 == count) {
                    requireNotNull(windows.poll()).onComplete()
                    tailWindowValuesCount.addAndGet(1 - skip)
                } else {
                    tailWindowValuesCount.addAndGet(1)
                }

                if (window != null && windowSubscribed != null && windowSubscribed.compareAndSet(false, true)) {
                    window.onComplete()
                }
            }

            override fun onComplete() {
                windows.forEach { it.onComplete() }
                emitter.onComplete()
                upstreamDisposableWrapper.dispose()
            }

            override fun onError(error: Throwable) {
                windows.forEach { it.onError(error) }
                emitter.onError(error)
                upstreamDisposableWrapper.dispose()
            }
        })
    }
}
