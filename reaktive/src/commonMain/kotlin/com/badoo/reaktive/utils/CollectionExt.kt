package com.badoo.reaktive.utils

internal fun <T> List<T>.replace(index: Int, element: T): List<T> =
    ArrayList(this)
        .apply { set(index, element) }

fun <T> List<T>.insert(index: Int, element: T): List<T> =
    if (index == size) {
        plus(element)
    } else {
        ArrayList<T>(size + 1)
            .also { list ->
                forEachIndexed { i, item ->
                    if (i == index) {
                        list.add(element)
                    }
                    list.add(item)
                }
            }
    }