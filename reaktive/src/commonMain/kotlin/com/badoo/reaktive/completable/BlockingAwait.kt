package com.badoo.reaktive.completable

import com.badoo.reaktive.maybe.blockingGet

/**
 * Blocks current thread until the current [Completable] completes or
 * fails with an exception (which is propagated).
 * ⚠️ Please note that this method is not available in JavaScript due to its single threaded nature.
 * A runtime exception will be thrown when this method is called in JavaScript. If you need this
 * in JavaScript for testing purposes, then consider using `Single.testAwait()` extension
 * from the `reaktive-testing` module.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#blockingAwait--).
 */
fun Completable.blockingAwait() {
    asMaybe<Unit>().blockingGet()
}
