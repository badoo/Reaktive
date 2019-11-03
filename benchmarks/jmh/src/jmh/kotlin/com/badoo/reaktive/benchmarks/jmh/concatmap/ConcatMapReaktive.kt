package com.badoo.reaktive.benchmarks.jmh.concatmap

import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.concatMap
import com.badoo.reaktive.observable.subscribe
import org.openjdk.jmh.annotations.Benchmark

open class ConcatMapReaktive {

    @Benchmark
    fun run() {
        val input = ConcatMapConfig.input.asObservable()

        input
            .concatMap { input }
            .subscribe()
    }
}
