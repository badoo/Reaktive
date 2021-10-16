package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.utils.atomic.AtomicInt

/**
 * When the [Single] signals `onSuccess`,
 * re-subscribes to the [Single] when the [Maybe] returned by the [handler] function emits a value.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#repeatWhen-io.reactivex.functions.Function-).
 */
fun <T> Single<T>.repeatWhen(handler: (repeatNumber: Int, value: T) -> Maybe<*>): Observable<T> =
    observable { emitter ->
        val observer =
            object : SingleObserver<T>, ErrorCallback by emitter {
                private val repeatNumber = AtomicInt()
                private val recursiveGuard = AtomicInt()

                private val repeatObserver: MaybeObserver<Any?> =
                    object : MaybeObserver<Any?>, Observer by this, CompletableCallbacks by emitter {
                        override fun onSuccess(value: Any?) {
                            emitter.tryCatch(block = ::subscribeToUpstream)
                        }
                    }

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.onNext(value)

                    emitter.tryCatch {
                        handler(repeatNumber.addAndGet(1), value).subscribe(repeatObserver)
                    }
                }

                fun subscribeToUpstream() {
                    // Prevents recursive subscriptions
                    if (recursiveGuard.addAndGet(1) == 1) {
                        do {
                            subscribe(this)
                        } while (recursiveGuard.addAndGet(-1) > 0)
                    }
                }
            }

        observer.subscribeToUpstream()
    }
