package com.badoo.reaktive.utils.atomic

import kotlin.reflect.KProperty

operator fun <R> AtomicBoolean.getValue(thisRef: R, property: KProperty<*>): Boolean = value

operator fun <R> AtomicBoolean.setValue(thisRef: R, property: KProperty<*>, value: Boolean) {
    this.value = value
}
