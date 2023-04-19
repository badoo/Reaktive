package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlin.reflect.KProperty

@InternalReaktiveApi
operator fun AtomicLong.getValue(thisRef: Any?, property: KProperty<*>): Long = value

@InternalReaktiveApi
operator fun AtomicLong.setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
    this.value = value
}
