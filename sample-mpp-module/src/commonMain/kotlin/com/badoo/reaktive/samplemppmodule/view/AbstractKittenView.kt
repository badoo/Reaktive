package com.badoo.reaktive.samplemppmodule.view

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.samplemppmodule.view.KittenView.Event
import com.badoo.reaktive.subject.publish.PublishSubject

abstract class AbstractKittenView : KittenView {

    private val _events = PublishSubject<Event>()
    override val events: Observable<Event> = _events

    fun dispatch(event: Event) {
        _events.onNext(event)
    }
}
