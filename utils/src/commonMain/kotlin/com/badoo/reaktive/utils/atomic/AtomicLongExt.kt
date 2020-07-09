package com.badoo.reaktive.utils.atomic

import kotlin.reflect.KProperty

operator fun <R> AtomicLong.getValue(thisRef: R, property: KProperty<*>): Long = value

operator fun <R> AtomicLong.setValue(thisRef: R, property: KProperty<*>, value: Long) {
    this.value = value
}
