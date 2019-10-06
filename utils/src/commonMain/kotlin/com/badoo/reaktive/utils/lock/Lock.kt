package com.badoo.reaktive.utils.lock

expect class Lock constructor() {

    fun acquire()

    fun release()

    fun destroy()

    fun newCondition(): Condition
}