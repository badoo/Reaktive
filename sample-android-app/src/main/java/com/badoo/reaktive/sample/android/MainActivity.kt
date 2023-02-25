package com.badoo.reaktive.sample.android

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.badoo.reaktive.disposable.scope.DisposableScope
import com.badoo.reaktive.samplemppmodule.Counter
import com.badoo.reaktive.samplemppmodule.Counter.Event

class MainActivity : AppCompatActivity(), DisposableScope by DisposableScope() {

    private val counter = Counter().scope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val countText: TextView = findViewById(R.id.text_count)
        val progressBar: ProgressBar = findViewById(R.id.progress_bar)
        val incrementButton: View = findViewById(R.id.button_increment)
        val decrementButton: View = findViewById(R.id.button_decrement)
        val resetButton: View = findViewById(R.id.button_reset)
        val fibonacciButton: View = findViewById(R.id.button_fibonacci)

        counter.state.subscribeScoped { state ->
            countText.text = state.value.toString()
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.INVISIBLE
        }

        incrementButton.setOnClickListener { counter.onEvent(Event.Increment) }
        decrementButton.setOnClickListener { counter.onEvent(Event.Decrement) }
        resetButton.setOnClickListener { counter.onEvent(Event.Reset) }
        fibonacciButton.setOnClickListener { counter.onEvent(Event.Fibonacci) }
    }

    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }
}
