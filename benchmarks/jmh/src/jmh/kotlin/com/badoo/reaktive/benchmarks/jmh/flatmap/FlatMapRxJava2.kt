package com.badoo.reaktive.benchmarks.jmh.flatmap

import io.reactivex.Observable
import org.openjdk.jmh.annotations.Benchmark

open class FlatMapRxJava2 {

    @Benchmark
    fun run() {
        val input = Observable.fromIterable(FlatMapConfig.input)

        input
            .flatMap { input }
            .subscribe()
    }
}
