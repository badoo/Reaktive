package com

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class Tests {

    @Test
    fun foo() {
    }
}

fun sleep(millis: Long) {
    try {
        Thread.sleep(millis)
    } catch (ignored: InterruptedException) {
        Thread.currentThread().interrupt()
    }
}