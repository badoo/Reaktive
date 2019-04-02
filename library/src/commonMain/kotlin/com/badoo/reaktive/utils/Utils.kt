package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal fun handleSourceError(error: Throwable, onError: ((Throwable) -> Unit)?) {
    try {
        if (onError == null) {
            reaktiveUncaughtErrorHandler(error)
        } else {
            try {
                onError(error)
            } catch (ignored: Throwable) {
                reaktiveUncaughtErrorHandler(error)
            }
        }
    } catch (e: Throwable) {
        println("Error delivering uncaught error: e")
    }
}

internal fun <T> synchronizedReadWriteProperty(initialValue: T, lock: Lock = newLock()): ReadWriteProperty<Any, T> =
    object : ReadWriteProperty<Any, T> {
        private var value: T = initialValue

        override fun getValue(thisRef: Any, property: KProperty<*>): T = lock.synchronized(::value)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            this.value = value
        }
    }