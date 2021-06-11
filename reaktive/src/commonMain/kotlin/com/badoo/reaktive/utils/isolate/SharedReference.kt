package com.badoo.reaktive.utils.isolate

import com.badoo.reaktive.disposable.Disposable

internal interface SharedReference<out T : Any> : Disposable {

    /**
     * May throw an exception if some access conditions are not met
     */
    fun getOrThrow(): T?
}
