package com.badoo.reaktive.test.single

import com.badoo.reaktive.completable.blockingAwait
import com.badoo.reaktive.single.Single

/**
 * This method is used when you need to wait for an asynchronous operation to finish
 * and you can't use or don't want to use the [TestScheduler][com.badoo.reaktive.test.scheduler.TestScheduler].
 *
 * It is JavaScript friendly, it returns `Promise` which, when returned from a test method,
 * causes the test to wait for completion. In all other targets [Completable.blockingAwait()][blockingAwait] is used.
 *
 * Please note the following factors:
 * - The [mainScheduler][com.badoo.reaktive.scheduler.mainScheduler] is not available in
 * Android unit tests and will crash if used;
 * - Darwin (Apple) tests are executed on the Main thread and so using the
 * [mainScheduler][com.badoo.reaktive.scheduler.mainScheduler] from this method will cause dead lock.
 *
 * To avoid the problems above use [overrideSchedulers][com.badoo.reaktive.scheduler.overrideSchedulers]
 * to replace schedulers with [TestScheduler][com.badoo.reaktive.test.scheduler.TestScheduler] or
 * with [TrampolineScheduler][com.badoo.reaktive.scheduler.TrampolineScheduler].
 *
 * Usage example:
 * ```
 * class MyLogic {
 *     fun div(a: Int, b: Int): Single<Int> =
 *         singleFromFunction { a / b }
 *             .subscribeOn(computationScheduler)
 * }
 *
 * class MyLogicTest {
 *     @Test
 *     fun returns_2_WHEN_div_6_by_3() =
 *         MyLogic()
 *             .div(6, 3)
 *             .testAwait { assertEquals(2, it) }
 *
 *     @Test
 *     fun throws_ArithmeticException_WHEN_div_by_0() =
 *         MyLogic()
 *             .div(6, 0)
 *             .testAwait(assertError = { assertTrue(it is ArithmeticException) }, assertSuccess = { fail("Did not throw") })
 * }
 * ```
 *
 * @receiver a [Single] for which the test should wait before completing
 * @param assertError when provided, it will be called in case of [Single] error and
 * the test will fail only when this callback throws an exception.
 * This gives an opportunity to assert the error.
 * @param assertSuccess when provided, it will be called in case of [Single] success.
 * This gives an opportunity to assert the result.
 */
expect fun <T> Single<T>.testAwait(assertError: ((Throwable) -> Unit)? = null, assertSuccess: (T) -> Unit)
