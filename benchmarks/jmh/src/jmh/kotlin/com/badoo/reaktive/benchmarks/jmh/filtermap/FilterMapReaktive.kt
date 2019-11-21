package com.badoo.reaktive.benchmarks.jmh.filtermap

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.filter
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.observable.subscribe
import org.openjdk.jmh.annotations.Benchmark

open class FilterMapReaktive {

    @Benchmark
    fun iterable() {
        FilterMapConfig
            .input
            .asObservable()
            .also(::run)
    }

    @Benchmark
    fun emitter() {
        observable<Int> { emitter ->
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
