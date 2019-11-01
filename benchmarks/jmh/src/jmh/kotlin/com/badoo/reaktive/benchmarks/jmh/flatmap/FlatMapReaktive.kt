package com.badoo.reaktive.benchmarks.jmh.flatmap

import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.subscribe
import org.openjdk.jmh.annotations.Benchmark

open class FlatMapReaktive {

    @Benchmark
    fun run() {
        val input = FlatMapConfig.input.asObservable()

        input
            .flatMap { input }
            .subscribe()
    }
}
