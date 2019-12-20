package com.badoo.reaktive.benchmarks.jmh.filtermap

import io.reactivex.Observable
import org.openjdk.jmh.annotations.Benchmark

open class FilterMapRxJava2 {

    @Benchmark
    fun iterable() {
        Observable
            .fromIterable(FilterMapConfig.input)
            .also(::run)
    }

    @Benchmark
    fun emitter() {
        Observable.create<Int> { emitter ->
            FilterMapConfig.input.forEach(emitter::onNext)
            emitter.onComplete()
        }
            .also(::run)
    }

    private fun run(input: Observable<Int>) {
        input
            .filter { it % 2 == 0 }
            .map { it * 2 }
            .subscribe()
    }
}
