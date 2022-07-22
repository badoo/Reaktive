package com.badoo.reaktive.completable

/**
 * Returns a [Completable] that for each subscription acquires a new resource via [resourceSupplier],
 * then calls [sourceSupplier] and subscribes to the returned upstream [Completable]
 * and disposes the resource via [sourceSupplier] when the upstream [Completable] is finished (either terminated or disposed).
 *
 * Please refer to the corresponding RxJava [document](https://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#using-java.util.concurrent.Callable-io.reactivex.functions.Function-io.reactivex.functions.Consumer-boolean-.
 */
fun <R> completableUsing(
    resourceSupplier: () -> R,
    resourceCleanup: (resource: R) -> Unit,
    eager: Boolean = true,
    sourceSupplier: (resource: R) -> Completable,
): Completable =
    completableDefer {
        val resource = resourceSupplier()

        sourceSupplier(resource).run {
            if (eager) {
                doOnBeforeFinally { resourceCleanup(resource) }
            } else {
                doOnAfterFinally { resourceCleanup(resource) }
            }
        }
    }
