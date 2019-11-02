package com.badoo.reaktive.benchmarks.jmh.filtermap

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.Benchmark

open class FilterMapFlow {

    @Benchmark
    fun run() {
        runBlocking {
            FilterMapConfig.input
                .asFlow()
                .filter { it % 2 == 0 }
                .map { it * 2 }
                .collect()
        }
    }
}
