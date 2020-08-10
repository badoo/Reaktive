package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.blockingAwait

/**
 * This method is used when you need to wait for an asynchronous operation to finish
 * and you can't use or don't want to use the [TestScheduler][com.badoo.reaktive.test.scheduler.TestScheduler].
 *
 * It is JavaScript friendly, it returns `Promise` which, when returned from a test method,
 * causes the test to wait for completion. In all other targets [Completable.blockingAwait()][blockingAwait] is used.
 *
 * Example:
 * ```
 * class Calculator {
 *     fun sum(a: Int, b: Int): Single<Int> =
 *         singleFromFunction { a + b }
 *             .subscribeOn(computationScheduler)
 * }
 *
 * class CalculatorTest {
 *     @Test
 *     fun sum_2_3_returns_5() =
 *         Calculator()
 *             .sum(2, 3)
 *             .doOnBeforeSuccess { assertEquals(6, it) }
 *             .asCompletable()
 *             .testAwait()
 * }
 * ```
 */
expect fun Completable.testAwait()
