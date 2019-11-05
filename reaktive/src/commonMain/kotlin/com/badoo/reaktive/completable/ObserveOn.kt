package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.freeze

fun Completable.observeOn(scheduler: Scheduler): Completable =
    completable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val executor = scheduler.newExecutor()
        disposables += executor

        subscribe(
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
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
