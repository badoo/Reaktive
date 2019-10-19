package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.utils.isFrozen

fun <T> Observable<T>.toList(): Single<List<T>> =
<<<<<<< HEAD
    collect(emptyList()) { list, item -> list + item }
=======
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
>>>>>>> 6e37dad3d71e0d11bc9b8015470548a5c10eecc6
