package com.badoo.reaktive.utils

import kotlin.time.Duration

@InternalReaktiveApi
fun Duration.coerceAtLeastZero(): Duration =
    if (isNegative()) Duration.ZERO else this
