package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.utils.isFrozen

/**
 * Returns a [Single] that emits a [List] containing all elements emitted by the **finite** source [Observable].
 *
 * ⚠️ Warning: if the emitted [List] becomes [frozen](https://github.com/badoo/Reaktive#kotlin-native-pitfalls)
 * somewhere in the downstream in Kotlin/Native, then the operator will copy the [List] on next iteration,
 * which can significantly affect performance.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#toList--).
 */
fun <T> Observable<T>.toList(): Single<List<T>> =
    collect(ArrayList()) { list, item ->
        if (list.isFrozen) {
            // We have to create a new ArrayList
            ArrayList<T>(list.size + 1).apply {
                addAll(list)
                add(item)
            }
        } else {
            list += item
            list
        }
    }
