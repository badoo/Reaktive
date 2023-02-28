package com.badoo.reaktive.utils

internal expect class SharedList<T>(initialCapacity: Int) : MutableList<T> {
    constructor()
}
