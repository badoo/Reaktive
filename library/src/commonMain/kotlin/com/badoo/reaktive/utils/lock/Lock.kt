package com.badoo.reaktive.utils.lock

internal interface Lock {

    fun acquire()

    fun release()
}