package com.badoo.reaktive.disposable

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

operator fun CompositeDisposable.minusAssign(disposable: Disposable) {
    remove(disposable)
}
