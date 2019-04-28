package com.badoo.reaktive.sample.linux

import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import platform.posix.sleep

/**
 * How to run: execute ":sample-linuxx64-app:runDebugExecutableLinux" Gradle task
 */
fun main() {

    observable<Int> { emitter ->
        repeat(5) {
            emitter.onNext(it)
            sleep(1)
        }
        emitter.onComplete()
    }
        .subscribeOn(ioScheduler)
        .observeOn(mainScheduler)
        .subscribe(onNext = ::println, onComplete = { println("complete") })

    sleep(6)
}