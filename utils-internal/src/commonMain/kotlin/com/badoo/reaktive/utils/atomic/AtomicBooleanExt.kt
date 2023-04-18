package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlin.reflect.KProperty

@InternalReaktiveApi
operator fun AtomicBoolean.getValue(thisRef: Any?, property: KProperty<*>): Boolean = value

@InternalReaktiveApi
operator fun AtomicBoolean.setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
    this.value = value
}
