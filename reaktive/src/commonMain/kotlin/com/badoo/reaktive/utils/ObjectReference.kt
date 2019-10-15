package com.badoo.reaktive.utils

internal expect class ObjectReference<T>(initialValue: T) {

    var value: T
}
