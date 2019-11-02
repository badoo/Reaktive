package com.badoo.reaktive.benchmarks.jmh.filtermap

import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.subscribe
import org.openjdk.jmh.annotations.Benchmark

open class FilterMapReaktive {

    @Benchmark
    fun run() {
        FilterMapConfig.input
            .asObservable()
            .filter { it % 2 == 0 }
            .map { it * 2 }
            .subscribe()
    }
}
