package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe
import com.badoo.reaktive.utils.ObjectReference
import com.badoo.reaktive.utils.Uninitialized

fun <T> Observable<T>.reduce(reducer: (a: T, b: T) -> T): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : ObjectReference<Any?>(Uninitialized), ObservableObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    getCurrentValue(onExists = emitter::onSuccess, onNotExists = emitter::onComplete)
                }

                override fun onNext(value: T) {
                    getCurrentValue(
                        onExists = { currentValue ->
                            emitter.tryCatch {
                                this.value = reducer(currentValue, value)
                            }
                        },
                        onNotExists = { this.value = value }
                    )
                }

                private inline fun getCurrentValue(onExists: (T) -> Unit, onNotExists: () -> Unit) {
                    val prevValue: Any? = value
                    if (prevValue === Uninitialized) {
                        onNotExists()
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        onExists(prevValue as T)
                    }
                }
            }
        )
    }
