package com.badoo.reaktive.plugin

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single

@ExperimentalReaktiveApi
interface ReaktivePlugin {

    fun <T> onAssembleObservable(observable: Observable<T>): Observable<T> = observable

    fun <T> onAssembleSingle(single: Single<T>): Single<T> = single

    fun <T> onAssembleMaybe(maybe: Maybe<T>): Maybe<T> = maybe

    fun onAssembleCompletable(completable: Completable): Completable = completable
}
