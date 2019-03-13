package com.arkivanov.rxkotlin.maybe

import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable

fun <T> Maybe<T>.toObservable(): Observable<T> =
    observable { observer ->
        subscribeSafe(
            object : MaybeObserver<T>, CompletableObserver by observer {
                override fun onSuccess(value: T) {
                    observer.onNext(value)
                    observer.onComplete()
                }
            }
        )
    }