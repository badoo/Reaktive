package com.badoo.reaktive.benchmarks.jmh.filtermap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.Benchmark

open class FilterMapFlow {

    @Benchmark
    fun iterable() {
        FilterMapConfig
            .input
            .asFlow()
            .also(::run)
    }

    @Benchmark
    fun emitter() {
        flow {
            FilterMapConfig.input.forEach { emit(it) }
        }
            .also(::run)
    }

    private fun run(input: Flow<Int>) {
        runBlocking {
            input
                .filter { it % 2 == 0 }
                .map { it * 2 }
                .collect()
        }
    }
}
