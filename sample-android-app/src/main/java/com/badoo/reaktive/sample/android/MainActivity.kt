package com.badoo.reaktive.sample.android

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.observableFromFunction
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.onErrorReturn
import com.badoo.reaktive.observable.startWithValue
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.observable.switchMap
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.badoo.reaktive.subject.publish.publishSubject
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val clickSubject = publishSubject<Unit>()
    private val disposables = CompositeDisposable()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.text)

        findViewById<Button>(R.id.button).setOnClickListener { clickSubject.onNext(Unit) }

        disposables += clickSubject
            .switchMap {
                observableFromFunction {
                    Thread.sleep(1000L)
                    return@observableFromFunction Date()
                }
                    .map(SimpleDateFormat.getDateTimeInstance()::format)
                    .subscribeOn(ioScheduler)
                    .onErrorReturn(Throwable::toString)
                    .startWithValue("Loading...")
            }
            .observeOn(mainScheduler)
            .subscribe(onNext = textView::setText)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

}
