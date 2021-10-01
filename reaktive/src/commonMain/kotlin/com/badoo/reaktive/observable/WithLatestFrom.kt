package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.Uninitialized
import com.badoo.reaktive.utils.atomic.atomicList
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.replace

/**
 * Combines the elements emitted by the source [Observable] with the latest emitted elements emitted by the
 * [others][others] [Observable]s. Elements are combined using the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#withLatestFrom-java.lang.Iterable-io.reactivex.functions.Function-).
 */
fun <T, U, R> Observable<T>.withLatestFrom(
    others: Iterable<Observable<U>>,
    mapper: (value: T, others: List<U>) -> R
): Observable<R> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)

        val otherSources = others.toList()
        val otherValues = atomicList<Any?>(List(otherSources.size) { Uninitialized })

        otherSources.forEachIndexed { index, source ->
            source.subscribe(
                object : ObservableObserver<U>, ErrorCallback by emitter {
                    override fun onSubscribe(disposable: Disposable) {
                        disposables += disposable
                    }

                    override fun onNext(value: U) {
                        otherValues.update {
                            it.replace(index, value)
                        }
                    }

                    override fun onComplete() {
                        // no-op
                    }
                }
            )
        }

        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onNext(value: T) {
                    val valueList =
                        otherValues
                            .value
                            .takeUnless { it.contains(Uninitialized) }
                            ?.let {
                                @Suppress("UNCHECKED_CAST")
                                it as List<U>
                            }
                            ?: return

                    emitter.tryCatch(block = { mapper(value, valueList) }, onSuccess = emitter::onNext)
                }
            }
        )
    }

/**
 * Combines the elements emitted by the source [Observable] with a latest emitted element emitted by the
 * [other][other] [Observable]. Elements are combined using the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#withLatestFrom-io.reactivex.ObservableSource-io.reactivex.functions.BiFunction-).
 */
fun <T, U, R> Observable<T>.withLatestFrom(other: Observable<U>, mapper: (T, U) -> R): Observable<R> =
    withLatestFrom(listOf(other)) { value, others ->
        mapper(value, others[0])
    }

/**
 * Combines the elements emitted by the source [Observable] with latest emitted elements emitted by
 * [other1] and [other2][other2] [Observable]s. Elements are combined using the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#withLatestFrom-io.reactivex.ObservableSource-io.reactivex.ObservableSource-io.reactivex.functions.Function3-).
 */
fun <T, T1, T2, R> Observable<T>.withLatestFrom(
    other1: Observable<T1>,
    other2: Observable<T2>,
    mapper: (value: T, other1: T1, other2: T2) -> R
): Observable<R> =
    withLatestFrom(listOf(other1, other2)) { value, others ->
        @Suppress("UNCHECKED_CAST")
        mapper(value, others[0] as T1, others[1] as T2)
    }

/**
 * Combines the elements emitted by the source [Observable] with latest emitted elements emitted by
 * [other1], [other2] and [other3][other3] [Observable]s. Elements are combined using the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#withLatestFrom-io.reactivex.ObservableSource-io.reactivex.ObservableSource-io.reactivex.ObservableSource-io.reactivex.functions.Function4-).
 */
fun <T, T1, T2, T3, R> Observable<T>.withLatestFrom(
    other1: Observable<T1>,
    other2: Observable<T2>,
    other3: Observable<T3>,
    mapper: (value: T, other1: T1, other2: T2, other3: T3) -> R
): Observable<R> =
    withLatestFrom(listOf(other1, other2, other3)) { value, others ->
        @Suppress("UNCHECKED_CAST")
        mapper(value, others[0] as T1, others[1] as T2, others[2] as T3)
    }
