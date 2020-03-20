package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicInt

fun <T> Observable<T>.buffer(count: Int, skip: Int = 0): Observable<List<T>> {
    require(count > 0) { "Count value must be positive" }
    require(skip >= 0) { "Skip value must not be negative" }

    return observable { emitter ->
        val list = SharedList<T>(count)
        val skipCounter = AtomicInt(0)

        subscribe(
            object : ObservableObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    if (skipCounter.value > 0) {
                        skipCounter.addAndGet(-1)
                    } else {
                        list += value
                        if (list.size == count) {
                            emitter.onNext(list.toList())
                            list.clear()
                            if (skip > 0) {
                                skipCounter.value = skip
                            }
                        }
                    }
                }

                override fun onComplete() {
                    if (list.isNotEmpty()) {
                        emitter.onNext(list)
                    }
                    emitter.onComplete()
                }
            }
        )
    }
}
