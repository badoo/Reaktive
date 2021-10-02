package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import com.badoo.reaktive.utils.ObjectReference

/**
 * Collects elements emitted by the **finite** source [Observable] into a data structure [C]
 * and returns a [Single] that emits this structure. The data structure can be mutable or immutable.
 * The [accumulator] should either mutate the structure and return the same reference,
 * or copy the structure and return a reference to the new copy.
 *
 * Please be aware that the structure may become [frozen](https://github.com/badoo/Reaktive#kotlin-native-pitfalls) in Kotlin/Native.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#collectInto-U-io.reactivex.functions.BiConsumer-).
 */
fun <T, C> Observable<T>.collect(initialCollection: C, accumulator: (C, T) -> C): Single<C> =
    single { emitter ->
        subscribe(
            object : ObjectReference<C>(initialCollection), ObservableObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    emitter.tryCatch {
                        this.value = accumulator(this.value, value)
                    }
                }

                override fun onComplete() {
                    emitter.onSuccess(value)
                }
            }
        )
    }
