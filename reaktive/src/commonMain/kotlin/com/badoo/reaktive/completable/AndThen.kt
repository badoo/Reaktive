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

/**
 * Returns a [Completable] that first runs this [Completable] and waits for its completion, then runs the other [Completable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#andThen-io.reactivex.CompletableSource-).
 */
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

/**
 * Returns a [Single] that first runs this [Completable] and waits for its completion, then runs the other [Single].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#andThen-io.reactivex.SingleSource-).
 */
fun <T> Completable.andThen(single: Single<T>): Single<T> =
    asSingle(Unit).flatMap { single }

/**
 * Returns an [Observable] that first runs this [Completable] and waits for its completion, then runs the other [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#andThen-io.reactivex.ObservableSource-).
 */
fun <T> Completable.andThen(observable: Observable<T>): Observable<T> =
    asSingle(Unit).flatMapObservable { observable }

/**
 * Returns an [Observable] that first runs this [Completable] and waits for its completion, then runs the other [Maybe].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#andThen-io.reactivex.MaybeSource-).
 */
fun <T> Completable.andThen(maybe: Maybe<T>): Maybe<T> =
    asSingle(Unit).flatMapMaybe { maybe }
