package com.badoo.reaktive.completable

class AsMaybeTests : CompletableToMaybeTests by CompletableToMaybeTests({ asMaybe<Nothing>() })
