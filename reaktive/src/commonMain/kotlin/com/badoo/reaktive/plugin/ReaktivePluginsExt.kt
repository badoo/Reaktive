package com.badoo.reaktive.plugin

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single

@ExperimentalReaktiveApi
fun ReaktivePlugins.register(plugin: ReaktivePlugin) {
    var list: MutableList<ReaktivePlugin>? = plugins
    if (list == null) {
        list = ArrayList()
        plugins = list
    }

    list.add(plugin)
}

@ExperimentalReaktiveApi
fun ReaktivePlugins.unregister(plugin: ReaktivePlugin) {
    plugins?.remove(plugin)
}

@ExperimentalReaktiveApi
fun <T> ReaktivePlugins.onAssembleObservable(observable: Observable<T>): Observable<T> =
    plugins
        ?.fold(observable) { src, plugin -> plugin.onAssembleObservable(src) }
        ?: observable

@ExperimentalReaktiveApi
fun <T> ReaktivePlugins.onAssembleSingle(single: Single<T>): Single<T> =
    plugins
        ?.fold(single) { src, plugin -> plugin.onAssembleSingle(src) }
        ?: single

@ExperimentalReaktiveApi
fun <T> ReaktivePlugins.onAssembleMaybe(maybe: Maybe<T>): Maybe<T> =
    plugins
        ?.fold(maybe) { src, plugin -> plugin.onAssembleMaybe(src) }
        ?: maybe

@ExperimentalReaktiveApi
fun ReaktivePlugins.onAssembleCompletable(completable: Completable): Completable =
    plugins
        ?.fold(completable) { src, plugin -> plugin.onAssembleCompletable(src) }
        ?: completable
