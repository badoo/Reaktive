package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.MaybeObserver
import com.badoo.reaktive.utils.atomic.AtomicInt

fun <T> Observable<T>.repeatWhen(handler: (repeatNumber: Int) -> Maybe<*>): Observable<T> =
    observable { emitter ->
        val observer =
            object : ObservableObserver<T>, ValueCallback<T> by emitter, ErrorCallback by emitter {
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

                override fun onComplete() {
                    emitter.tryCatch {
                        handler(repeatNumber.addAndGet(1)).subscribe(repeatObserver)
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
