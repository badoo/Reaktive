package com.badoo.reaktive.sample.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.ObservableMiddleware
import com.badoo.reaktive.observable.ObservableObserver
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.observableOf
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.samplemppmodule.KittenStoreBuilderImpl
import com.badoo.reaktive.samplemppmodule.binder.KittenBinder
import com.badoo.reaktive.scheduler.computationScheduler

class MainActivity : AppCompatActivity() {

    private lateinit var kittenBinder: KittenBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ObservableMiddleware.register(
            object : ObservableMiddleware.Middleware {
                override fun <T> wrap(observable: Observable<T>): Observable<T> {
                    val stackTrace = Exception("Debug exception generated at call site")

                    return object : Observable<T> {
                        override fun subscribe(observer: ObservableObserver<T>) {
                            observable.subscribe(
                                object : ObservableObserver<T> by observer {
                                    override fun onError(error: Throwable) {
                                        error.addSuppressed(stackTrace)
                                        observer.onError(error)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        )

        foo()

        setContentView(R.layout.activity_main)

        kittenBinder = KittenBinder(KittenStoreBuilderImpl())
        kittenBinder.onViewCreated(KittenViewImpl(findViewById(android.R.id.content)))
    }

    fun foo() {
        observableOf(0)
            .observeOn(computationScheduler)
            .map { error("Kek!") }
            .subscribe()
    }

    override fun onStart() {
        super.onStart()

        kittenBinder.onStart()
    }

    override fun onStop() {
        kittenBinder.onStop()

        super.onStop()
    }

    override fun onDestroy() {
        kittenBinder.onDestroy()

        super.onDestroy()
    }
}
