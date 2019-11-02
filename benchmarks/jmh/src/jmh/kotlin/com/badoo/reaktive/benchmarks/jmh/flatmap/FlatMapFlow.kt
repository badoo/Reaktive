package com.badoo.reaktive.benchmarks.jmh.flatmap

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.Benchmark

open class FlatMapFlow {

    @Benchmark
    fun run() {
        runBlocking {
            val input = FlatMapConfig.input.asFlow()

            input
                .flatMapMerge { input }
                .collect()
        }
    }
}
