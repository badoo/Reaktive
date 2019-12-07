package com.badoo.reaktive.completable

class AsObservableTests : CompletableToObservableTests by CompletableToObservableTestsImpl({ asObservable<Nothing>() })
