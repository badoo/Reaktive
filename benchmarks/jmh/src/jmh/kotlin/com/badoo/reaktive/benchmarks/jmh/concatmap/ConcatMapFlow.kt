package com.badoo.reaktive.benchmarks.jmh.concatmap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.openjdk.jmh.annotations.Benchmark

open class ConcatMapFlow {

    @Benchmark
    fun iterable() {
        ConcatMapConfig
            .input
            .asFlow()
            .also(::run)
    }

    @Benchmark
    fun emitter() {
        flow {
            ConcatMapConfig.input.forEach { emit(it) }
        }
            .also(::run)
    }

    private fun run(input: Flow<Int>) {
        runBlocking {
            input
                .flatMapConcat { input }
                .collect()
        }
    }
}
