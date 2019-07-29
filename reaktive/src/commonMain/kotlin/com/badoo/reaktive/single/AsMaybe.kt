package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybeUnsafe

fun <T> Single<T>.asMaybe(): Maybe<T> =
    maybeUnsafe { observer ->
        subscribeSafe(
            object : SingleObserver<T>, Observer by observer, SuccessCallback<T> by observer, ErrorCallback by observer {
            }
        )
    }