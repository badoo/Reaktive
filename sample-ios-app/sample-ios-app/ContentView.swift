//
//  ContentView.swift
//  sample-ios-app
//
//  Created by Arkadii Ivanov on 25/02/2023.
//

import SwiftUI
import shared

struct ContentView: View {
    @StateObject
    private var counterHost = CounterHost()
    
    private var counter: Counter { counterHost.counter }
    private var state: Counter.State { counterHost.state }
    
    var body: some View {
        VStack {
            Text(String(state.value))
            
            ProgressView()
                .hidden(!state.isLoading)
            
            Spacer()
                .frame(height: 16)
                        
            Button("+") { counter.onEvent(event: CounterEventIncrement.shared) }
                .buttonStyle(.borderedProminent)
                .controlSize(.regular)

            Button("-") { counter.onEvent(event: CounterEventDecrement.shared) }
                .buttonStyle(.borderedProminent)
                .controlSize(.regular)

            Button("0") { counter.onEvent(event: CounterEventReset.shared) }
                .buttonStyle(.borderedProminent)
                .controlSize(.regular)

            Button("Fibonacci") { counter.onEvent(event: CounterEventFibonacci.shared) }
                .buttonStyle(.borderedProminent)
                .controlSize(.regular)
        }
        .padding()
    }
}

extension View {
    @ViewBuilder func hidden(_ hidden: Bool) -> some View {
        if (hidden) {
            self.hidden()
        } else {
            self
        }
    }
}

public final class CounterHost: ObservableObject {
    let counter: Counter = Counter()
    private var disposable: Disposable?
    
    @Published
    var state: Counter.State
    
    init() {
        state = counter.state.value
     
        disposable = counter.state.subscribe(
            isThreadLocal: true,
            onNext: { [weak self] value in self?.state = value }
        )
    }
    
    deinit {
        disposable?.dispose()
        disposable = nil
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
