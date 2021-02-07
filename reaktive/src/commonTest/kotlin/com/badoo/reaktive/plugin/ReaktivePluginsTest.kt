package com.badoo.reaktive.plugin

import com.badoo.reaktive.annotations.ExperimentalReaktiveApi
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completableUnsafe
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybeUnsafe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observableUnsafe
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.singleUnsafe
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalReaktiveApi::class)
class ReaktivePluginsTest {

    @AfterTest
    fun after() {
        plugins = null
    }

    @Test
    fun adds_registered_plugins() {
        val plugin1 = Plugin()
        val plugin2 = Plugin()

        registerReaktivePlugin(plugin1)
        registerReaktivePlugin(plugin2)

        assertTrue(plugins?.contains(plugin1) == true)
        assertTrue(plugins?.contains(plugin2) == true)
    }

    @Test
    fun removes_unregistered_plugins() {
        val plugin1 = Plugin()
        val plugin2 = Plugin()
        val plugin3 = Plugin()
        registerReaktivePlugin(plugin1)
        registerReaktivePlugin(plugin2)
        registerReaktivePlugin(plugin3)

        unregisterReaktivePlugin(plugin1)
        unregisterReaktivePlugin(plugin3)

        assertFalse(plugins?.contains(plugin1) == true)
        assertFalse(plugins?.contains(plugin3) == true)
    }

    @Test
    fun keeps_not_unregistered_plugins() {
        val plugin1 = Plugin()
        val plugin2 = Plugin()
        val plugin3 = Plugin()
        registerReaktivePlugin(plugin1)
        registerReaktivePlugin(plugin2)
        registerReaktivePlugin(plugin3)

        unregisterReaktivePlugin(plugin1)
        unregisterReaktivePlugin(plugin3)

        assertTrue(plugins?.contains(plugin2) == true)
    }

    @Test
    fun onAssembleObservable_wraps_all_plugins_in_order() {
        val plugin1 =
            object : ReaktivePlugin {
                override fun <T> onAssembleObservable(observable: Observable<T>): Observable<T> = WrappedObservable(observable, 1)
            }

        val plugin2 =
            object : ReaktivePlugin {
                override fun <T> onAssembleObservable(observable: Observable<T>): Observable<T> = WrappedObservable(observable, 2)
            }

        registerReaktivePlugin(plugin1)
        registerReaktivePlugin(plugin2)
        val original = observableUnsafe<Nothing> {}

        val wrapped = onAssembleObservable(original)

        assertEquals(2, wrapped.wrappedId)
        assertEquals(1, wrapped.wrappedParent?.wrappedId)
        assertSame(original, wrapped.wrappedParent?.wrappedParent)
    }

    @Test
    fun onAssembleSingle_wraps_all_plugins_in_order() {
        val plugin1 =
            object : ReaktivePlugin {
                override fun <T> onAssembleSingle(single: Single<T>): Single<T> = WrappedSingle(single, 1)
            }

        val plugin2 =
            object : ReaktivePlugin {
                override fun <T> onAssembleSingle(single: Single<T>): Single<T> = WrappedSingle(single, 2)
            }

        registerReaktivePlugin(plugin1)
        registerReaktivePlugin(plugin2)
        val original = singleUnsafe<Nothing> {}

        val wrapped = onAssembleSingle(original)

        assertEquals(2, wrapped.wrappedId)
        assertEquals(1, wrapped.wrappedParent?.wrappedId)
        assertSame(original, wrapped.wrappedParent?.wrappedParent)
    }

    @Test
    fun onAssembleMaybe_wraps_all_plugins_in_order() {
        val plugin1 =
            object : ReaktivePlugin {
                override fun <T> onAssembleMaybe(maybe: Maybe<T>): Maybe<T> = WrappedMaybe(maybe, 1)
            }

        val plugin2 =
            object : ReaktivePlugin {
                override fun <T> onAssembleMaybe(maybe: Maybe<T>): Maybe<T> = WrappedMaybe(maybe, 2)
            }

        registerReaktivePlugin(plugin1)
        registerReaktivePlugin(plugin2)
        val original = maybeUnsafe<Nothing> {}

        val wrapped = onAssembleMaybe(original)

        assertEquals(2, wrapped.wrappedId)
        assertEquals(1, wrapped.wrappedParent?.wrappedId)
        assertSame(original, wrapped.wrappedParent?.wrappedParent)
    }

    @Test
    fun onAssembleCompletable_wraps_all_plugins_in_order() {
        val plugin1 =
            object : ReaktivePlugin {
                override fun onAssembleCompletable(completable: Completable): Completable = WrappedCompletable(completable, 1)
            }

        val plugin2 =
            object : ReaktivePlugin {
                override fun onAssembleCompletable(completable: Completable): Completable = WrappedCompletable(completable, 2)
            }

        registerReaktivePlugin(plugin1)
        registerReaktivePlugin(plugin2)
        val original = completableUnsafe {}

        val wrapped = onAssembleCompletable(original)

        assertEquals(2, wrapped.wrappedId)
        assertEquals(1, wrapped.wrappedParent?.wrappedId)
        assertSame(original, wrapped.wrappedParent?.wrappedParent)
    }

    private val <T> Observable<T>.wrappedParent: Observable<T>? get() = (this as? WrappedObservable<T>)?.parent

    private val <T> Observable<T>.wrappedId: Int? get() = (this as? WrappedObservable<T>)?.id

    private val <T> Single<T>.wrappedParent: Single<T>? get() = (this as? WrappedSingle<T>)?.parent

    private val <T> Single<T>.wrappedId: Int? get() = (this as? WrappedSingle<T>)?.id

    private val <T> Maybe<T>.wrappedParent: Maybe<T>? get() = (this as? WrappedMaybe<T>)?.parent

    private val <T> Maybe<T>.wrappedId: Int? get() = (this as? WrappedMaybe<T>)?.id

    private val Completable.wrappedParent: Completable? get() = (this as? WrappedCompletable)?.parent

    private val Completable.wrappedId: Int? get() = (this as? WrappedCompletable)?.id

    private class Plugin : ReaktivePlugin

    private class WrappedObservable<T>(val parent: Observable<T>, val id: Int) : Observable<T> by parent

    private class WrappedSingle<T>(val parent: Single<T>, val id: Int) : Single<T> by parent

    private class WrappedMaybe<T>(val parent: Maybe<T>, val id: Int) : Maybe<T> by parent

    private class WrappedCompletable(val parent: Completable, val id: Int) : Completable by parent
}
