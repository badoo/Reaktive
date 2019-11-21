package com.badoo.reaktive.utils

internal expect open class PairReference<T, R>(firstInitial: T, secondInitial: R) {

    var first: T
    var second: R
}
