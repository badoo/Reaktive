package com.badoo.reaktive.completable

class AsMaybeTests : CompletableToMaybeTests by CompletableToMaybeTestsImpl({ asMaybe<Nothing>() })
