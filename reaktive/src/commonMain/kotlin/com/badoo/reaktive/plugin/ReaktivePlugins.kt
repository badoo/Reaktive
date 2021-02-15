@file:JvmName("ReaktivePluginsJvm")

package com.badoo.reaktive.plugin

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.utils.SharedList
import kotlin.jvm.JvmName

@ExperimentalReaktiveApi
internal expect var plugins: SharedList<ReaktivePlugin>?

@ExperimentalReaktiveApi
fun registerReaktivePlugin(plugin: ReaktivePlugin) {
    var list = plugins
    if (list == null) {
        list = SharedList(0)
        plugins = list
    }
    list.add(plugin)
}

@ExperimentalReaktiveApi
fun unregisterReaktivePlugin(plugin: ReaktivePlugin) {
    plugins?.also {
        it.remove(plugin)
        if (it.isEmpty()) {
            plugins = null
        }
    }
}

@ExperimentalReaktiveApi
fun <T> onAssembleObservable(observable: Observable<T>): Observable<T> =
    plugins
        ?.fold(observable) { source, plugin -> plugin.onAssembleObservable(source) }
        ?: observable

@ExperimentalReaktiveApi
fun <T> onAssembleSingle(single: Single<T>): Single<T> =
    plugins
        ?.fold(single) { src, plugin -> plugin.onAssembleSingle(src) }
        ?: single

@ExperimentalReaktiveApi
fun <T> onAssembleMaybe(maybe: Maybe<T>): Maybe<T> =
    plugins
        ?.fold(maybe) { src, plugin -> plugin.onAssembleMaybe(src) }
        ?: maybe

@ExperimentalReaktiveApi
fun onAssembleCompletable(completable: Completable): Completable =
    plugins
        ?.fold(completable) { src, plugin -> plugin.onAssembleCompletable(src) }
        ?: completable
