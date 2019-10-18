package com.badoo.reaktive.maybe

inline fun <reified T> Maybe<*>.ofType(): Maybe<T> =
    filter { it is T }
        .map { it as T }
        