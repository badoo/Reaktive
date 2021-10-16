package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.BufferedExecutor
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.freeze

/**
 * Signals all events of the [Observable] on the specified [Scheduler].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#observeOn-io.reactivex.Scheduler-).
 */
fun <T> Observable<T>.observeOn(scheduler: Scheduler): Observable<T> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor
        val bufferedExecutor = BufferedExecutor(executor, emitter::onNext)
        disposables += bufferedExecutor

        subscribe(
            object : ObservableObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onNext(value: T) {
                    bufferedExecutor.submit(value)
                }

                override fun onComplete() {
                    executor.submit {
                        emitter.onComplete()
                    }
                }

                override fun onError(error: Throwable) {
                    error.freeze()

                    executor.submit {
                        emitter.onError(error)
                    }
                }
            }
        )
    }
