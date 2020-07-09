package com.badoo.reaktive.utils.atomic

import kotlin.reflect.KProperty

operator fun <R> AtomicInt.getValue(thisRef: R, property: KProperty<*>): Int = value

operator fun <R> AtomicInt.setValue(thisRef: R, property: KProperty<*>, value: Int) {
    this.value = value
}
