package com.badoo.reaktive.sample.android

import android.app.Application
import com.badoo.reaktive.rxjavainterop.toReaktive
import com.badoo.reaktive.scheduler.overrideSchedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        overrideSchedulers(
            main = { AndroidSchedulers.mainThread().toReaktive() },
            computation = { Schedulers.computation().toReaktive() },
            io = { Schedulers.io().toReaktive() }
        )
    }
}