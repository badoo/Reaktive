package com.badoo.reaktive.completable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.flatMap
import com.badoo.reaktive.single.flatMapMaybe
import com.badoo.reaktive.single.flatMapObservable

fun Completable.andThen(completable: Completable): Completable =
    completable { emitter ->
        subscribe(
            object : CompletableObserver, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    completable.subscribeSafe(
                        object : CompletableObserver, Observer by this, CompletableCallbacks by emitter {
                        }
                    )
                }
            }
        )
    }

fun <T> Completable.andThen(single: Single<T>): Single<T> =
    asSingle(Unit).flatMap { single }

fun <T> Completable.andThen(observable: Observable<T>): Observable<T> =
    asSingle(Unit).flatMapObservable { observable }

fun <T> Completable.andThen(maybe: Maybe<T>): Maybe<T> =
    asSingle(Unit).flatMapMaybe { maybe }
