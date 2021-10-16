package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler

/**
 * Returns a [Maybe] that subscribes to the source [Maybe] on the specified [Scheduler].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#subscribeOn-io.reactivex.Scheduler-).
 */
fun <T> Maybe<T>.subscribeOn(scheduler: Scheduler): Maybe<T> =
    maybe { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor

        executor.submit {
            subscribeSafe(
                object : MaybeObserver<T>, MaybeCallbacks<T> by emitter {
                    override fun onSubscribe(disposable: Disposable) {
                        disposables += disposable
                    }
                }
            )
        }
    }
