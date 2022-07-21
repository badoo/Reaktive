package com.badoo.reaktive.maybe

/**
 * Returns a [Maybe] that for each subscription acquires a new resource via [resourceSupplier],
 * then calls [sourceSupplier] and subscribes to the returned upstream [Maybe]
 * and disposes the resource via [sourceSupplier] when the upstream [Maybe] is finished (either terminated or disposed).
 *
 * Please refer to the corresponding RxJava [document](https://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#using-java.util.concurrent.Callable-io.reactivex.functions.Function-io.reactivex.functions.Consumer-boolean-).
 */
fun <T, R> maybeUsing(
    resourceSupplier: () -> R,
    resourceCleanup: (resource: R) -> Unit,
    eager: Boolean = true,
    sourceSupplier: (resource: R) -> Maybe<T>,
): Maybe<T> =
    maybeDefer {
        val resource = resourceSupplier()

        sourceSupplier(resource).run {
            if (eager) {
                doOnBeforeFinally { resourceCleanup(resource) }
            } else {
                doOnAfterFinally { resourceCleanup(resource) }
            }
        }
    }
