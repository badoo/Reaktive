package com.badoo.reaktive.utils

internal actual open class PairReference<T, R> actual constructor(firstInitial: T, secondInitial: R) {

    actual var first: T = firstInitial
    actual var second: R = secondInitial
}
