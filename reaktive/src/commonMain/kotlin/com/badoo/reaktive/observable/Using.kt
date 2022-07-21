package com.badoo.reaktive.observable

/**
 * Returns an [Observable] that for each subscription acquires a new resource via [resourceSupplier],
 * then calls [sourceSupplier] and subscribes to the returned upstream [Observable]
 * and disposes the resource via [sourceSupplier] when the upstream [Observable] is finished (either terminated or disposed).
 *
 * Please refer to the corresponding RxJava [document](https://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#using-java.util.concurrent.Callable-io.reactivex.functions.Function-io.reactivex.functions.Consumer-boolean-).
 */
fun <T, R> observableUsing(
    resourceSupplier: () -> R,
    resourceCleanup: (resource: R) -> Unit,
    eager: Boolean = true,
    sourceSupplier: (resource: R) -> Observable<T>,
): Observable<T> =
    observableDefer {
        val resource = resourceSupplier()

        sourceSupplier(resource).run {
            if (eager) {
                doOnBeforeFinally { resourceCleanup(resource) }
            } else {
                doOnAfterFinally { resourceCleanup(resource) }
            }
        }
    }
