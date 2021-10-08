package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.queue.SharedQueue

/**
 * Returns an [Observable] that emits buffered [List]s of elements it collects from the source [Observable].
 * The first buffer is started with the first element emitted by the source [Observable].
 * Every subsequent buffer is started every [skip] elements, making overlapping buffers possible.
 * Buffers are emitted once the size reaches [count] elements.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#buffer-int-int-).
 */
fun <T> Observable<T>.buffer(count: Int, skip: Int = count): Observable<List<T>> {
    require(count > 0) { "Count value must be positive" }
    require(skip > 0) { "Skip value must be positive" }

    return observable { emitter ->
        val listQueue = SharedQueue<SharedList<T>>()
        val skipCounter = AtomicInt(1)

        subscribe(
            object : ObservableObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    if (skipCounter.addAndGet(-1) == 0) {
                        skipCounter.value = skip
                        listQueue.offer(SharedList())
                    }

                    listQueue.forEach { it += value }

                    if (listQueue.peek?.size == count) {
                        pollAndEmit()
                    }
                }

                override fun onComplete() {
                    while (!listQueue.isEmpty) {
                        pollAndEmit()
                    }
                    emitter.onComplete()
                }

                private fun pollAndEmit() {
                    val list = listQueue.poll()!!
                    emitter.onNext(list)
                }
            }
        )
    }
}
