package com.badoo.reaktive.benchmarks.jmh.concatmap

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.concatMap
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.observable.subscribe
import org.openjdk.jmh.annotations.Benchmark

open class ConcatMapReaktive {

    @Benchmark
    fun iterable() {
        ConcatMapConfig
            .input
            .asObservable()
            .also(::run)
    }

    @Benchmark
    fun emitter() {
        observable<Int> { emitter ->
            ConcatMapConfig.input.forEach(emitter::onNext)
            emitter.onComplete()
        }
            .also(::run)
    }

    private fun run(input: Observable<Int>) {
        input
            .concatMap { input }
            .subscribe()
    }
}
