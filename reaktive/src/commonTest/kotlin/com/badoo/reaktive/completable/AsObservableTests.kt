package com.badoo.reaktive.completable

class AsObservableTests : CompletableToObservableTests by CompletableToObservableTests({ asObservable<Nothing>() })