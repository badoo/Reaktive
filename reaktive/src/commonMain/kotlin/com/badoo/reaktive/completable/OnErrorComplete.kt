package com.badoo.reaktive.completable

fun Completable.onErrorComplete(): Completable = onErrorResumeNext { completableOfEmpty() }
