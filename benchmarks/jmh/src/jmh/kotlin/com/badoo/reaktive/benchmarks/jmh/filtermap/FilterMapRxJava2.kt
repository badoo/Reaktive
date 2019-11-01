package com.badoo.reaktive.benchmarks.jmh.filtermap

import io.reactivex.Observable
import org.openjdk.jmh.annotations.Benchmark

open class FilterMapRxJava2 {

    @Benchmark
    fun run() {
        Observable
            .fromIterable(FilterMapConfig.input)
            .filter { it % 2 == 0 }
            .map { it * 2 }
            .subscribe()
    }
}
