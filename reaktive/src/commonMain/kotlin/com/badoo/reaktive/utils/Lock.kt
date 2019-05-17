package com.badoo.reaktive.utils

internal expect class Lock constructor() {

    fun acquire()

    fun release()

    fun destroy()

    fun newCondition(): Condition
}