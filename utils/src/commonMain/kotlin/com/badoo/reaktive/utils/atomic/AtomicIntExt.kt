package com.badoo.reaktive.utils.atomic

import kotlin.reflect.KProperty

operator fun AtomicInt.getValue(thisRef: Any?, property: KProperty<*>): Int = value

operator fun AtomicInt.setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
    this.value = value
}
