package com.badoo.reaktive.utils

@RequiresOptIn(
    message = "This API is internal, please don't use it",
    level = RequiresOptIn.Level.ERROR,
)
@Retention(AnnotationRetention.BINARY)
annotation class InternalReaktiveApi
