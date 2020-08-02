package com.badoo.reaktive.utils.atomic

import kotlin.reflect.KProperty

operator fun AtomicLong.getValue(thisRef: Any?, property: KProperty<*>): Long = value

operator fun AtomicLong.setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
    this.value = value
}
