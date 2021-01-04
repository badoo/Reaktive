## Reaktive and Swift interoperability

Reaktive can be exported and used in Swift as usual, however there are few limitations.

### Exporting Reaktive to Swift

Reaktive should be exported to Swift if you want to use it there.
The following Gradle configuration can be used as a reference.

```Kotlin
kotlin {
    ios {
        binaries {
            framework {
                // Some setup code here

                when (val target = this.compilation.target.name) {
                    "iosX64" -> {
                        export("com.badoo.reaktive:reaktive-iossim:<version>")
                    }

                    "iosArm64" -> {
                        export("com.badoo.reaktive:reaktive-ios64:<version>")
                    }

                    else -> error("Unsupported target: $target")
                }
            }
        }
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                api("com.badoo.reaktive:reaktive:<version>")
            }
        }
    }
}
```

### Exposing Reaktive sources to Swift

Reaktive sources (`Observable`, `Single`, `Maybe` and `Completable`) are Kotlin interfaces with generic types.
Since generics for interfaces are [not exported](https://kotlinlang.org/docs/reference/native/objc_interop.html#generics) to Swift,
Reaktive provides wrapper classes.

You can wrap Reaktive sources using corresponding `wrap()` extension functions:

- `Observable<T>.wrap(): ObservableWrapper<T>`
- `Single<T>.wrap(): SingleWrapper<T>`
- `Maybe<T>.wrap(): MaybeWrapper<T>`
- `Completable.wrap(): CompletableWrapper`

Example:

```Kotlin
class SharedDataSource {
    fun load(): SingleWrapper<String> =
        singleFromFunction {
            // A long running operation
            "A result"
        }
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .wrap()
}
```

### Using wrappers in Swift

Reaktive wrappers can be used in Swift as usual:

```Swift
func foo() {
    let ds = SharedDataSource()

    let disposable = ds.load().subscribe(isThreadLocal: false) { result in
        // Handle the result
    }

    // At some point later
    disposable.dispose()
}
```

### RxSwift interop

There is no published Reaktive-RxSwift interop modules currently,
please see [#538](https://github.com/badoo/Reaktive/issues/538) for some explanation.
However feel free to copy-paste the following solution.

```Swift
import RxSwift
import YourKotlinFramework

extension RxSwift.Observable where Element : AnyObject {
    static func from(_ observable: ObservableWrapper<Element>) -> RxSwift.Observable<Element> {
        return RxSwift.Observable<Element>.create { observer in
            let disposable = observable.subscribe(
                isThreadLocal: false,
                onError: { observer.onError(KotlinError($0)) },
                onComplete: observer.onCompleted,
                onNext: observer.onNext
            )

            return Disposables.create(with: disposable.dispose)
        }
    }
}

extension RxSwift.Single where Element : AnyObject {
    static func from(_ single: SingleWrapper<Element>) -> RxSwift.Single<Element> {
        return RxSwift.Single<Element>.create { observer in
            let disposable = single.subscribe(
                isThreadLocal: false,
                onError: { observer(.failure(KotlinError($0))) },
                onSuccess: { observer(.success($0)) }
            )

            return Disposables.create(with: disposable.dispose)
        }
    }
}

extension RxSwift.Maybe where Element : AnyObject {
    static func from(_ maybe: MaybeWrapper<Element>) -> RxSwift.Maybe<Element> {
        return RxSwift.Maybe<Element>.create { observer in
            let disposable = maybe.subscribe(
                isThreadLocal: false,
                onError: { observer(.error(KotlinError($0))) },
                onComplete: { observer(.completed) },
                onSuccess: { observer(.success($0)) }
            )

            return Disposables.create(with: disposable.dispose)
        }
    }
}

extension RxSwift.Completable {
    static func from(_ completable: CompletableWrapper) -> RxSwift.Completable {
        return RxSwift.Completable.create { observer in
            let disposable = completable.subscribe(
                isThreadLocal: false,
                onError: { observer(.error(KotlinError($0))) },
                onComplete: { observer(.completed) }
            )

            return Disposables.create(with: disposable.dispose)
        }
    }
}

struct KotlinError : Error {
    let throwable: KotlinThrowable

    init (_ throwable: KotlinThrowable) {
        self.throwable = throwable
    }
}
```
