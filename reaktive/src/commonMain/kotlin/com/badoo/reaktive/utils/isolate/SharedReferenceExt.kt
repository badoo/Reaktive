package com.badoo.reaktive.utils.isolate

import kotlin.reflect.KProperty

internal operator fun <T : Any> SharedReference<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value
