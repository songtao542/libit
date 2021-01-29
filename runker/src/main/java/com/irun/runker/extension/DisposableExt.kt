package com.irun.runker.extension

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.disposedBy(compositeDisposable: CompositeDisposable?): Boolean {
    return compositeDisposable?.add(this) ?: false
}