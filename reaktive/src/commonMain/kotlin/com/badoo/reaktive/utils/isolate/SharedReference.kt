package com.badoo.reaktive.utils.isolate

internal interface SharedReference<out T : Any> {

    val value: T
}
