package com.badoo.reaktive.single

/**
 * Returns a [Single] that for each subscription acquires a new resource via [resourceSupplier],
 * then calls [sourceSupplier] and subscribes to the returned upstream [Single]
 * and disposes the resource via [sourceSupplier] when the upstream [Single] is finished (either terminated or disposed).
 *
 * Please refer to the corresponding RxJava [document](https://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#using-java.util.concurrent.Callable-io.reactivex.functions.Function-io.reactivex.functions.Consumer-boolean-).
 */
fun <T, R> singleUsing(
    resourceSupplier: () -> R,
    resourceCleanup: (resource: R) -> Unit,
    eager: Boolean = true,
    sourceSupplier: (resource: R) -> Single<T>,
): Single<T> =
    singleDefer {
        val resource = resourceSupplier()

        sourceSupplier(resource).run {
            if (eager) {
                doOnBeforeFinally { resourceCleanup(resource) }
            } else {
                doOnAfterFinally { resourceCleanup(resource) }
            }
        }
    }
