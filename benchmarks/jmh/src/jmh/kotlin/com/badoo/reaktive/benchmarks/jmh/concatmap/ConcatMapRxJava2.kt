package com.badoo.reaktive.benchmarks.jmh.concatmap

import io.reactivex.Observable
import org.openjdk.jmh.annotations.Benchmark

open class ConcatMapRxJava2 {

    @Benchmark
    fun run() {
        val input = Observable.fromIterable(ConcatMapConfig.input)

        input
            .flatMap { input }
            .subscribe()
    }
}
