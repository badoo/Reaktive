package com.badoo.reaktive.utils.atomic

import kotlin.reflect.KProperty

operator fun AtomicBoolean.getValue(thisRef: Any?, property: KProperty<*>): Boolean = value

operator fun AtomicBoolean.setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
    this.value = value
}
