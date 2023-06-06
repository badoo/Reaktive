@file:JvmName("ReaktivePluginsJvm")

package com.badoo.reaktive.plugin

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single
import kotlin.jvm.JvmName

internal var plugins: ArrayList<ReaktivePlugin>? = null

fun registerReaktivePlugin(plugin: ReaktivePlugin) {
    var list = plugins
    if (list == null) {
        list = ArrayList()
        plugins = list
    }
    list.add(plugin)
}

fun unregisterReaktivePlugin(plugin: ReaktivePlugin) {
    plugins?.also {
        it.remove(plugin)
        if (it.isEmpty()) {
            plugins = null
        }
    }
}

fun <T> onAssembleObservable(observable: Observable<T>): Observable<T> =
    plugins
        ?.fold(observable) { source, plugin -> plugin.onAssembleObservable(source) }
        ?: observable

fun <T> onAssembleSingle(single: Single<T>): Single<T> =
    plugins
        ?.fold(single) { src, plugin -> plugin.onAssembleSingle(src) }
        ?: single

fun <T> onAssembleMaybe(maybe: Maybe<T>): Maybe<T> =
    plugins
        ?.fold(maybe) { src, plugin -> plugin.onAssembleMaybe(src) }
        ?: maybe

fun onAssembleCompletable(completable: Completable): Completable =
    plugins
        ?.fold(completable) { src, plugin -> plugin.onAssembleCompletable(src) }
        ?: completable
