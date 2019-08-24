import Foundation
import ReactiveSample

class KittenStoreBuilderImpl: KittenStoreBuilder {
    func build() -> KittenStore {
        return KittenStoreImpl(loader: KittenLoaderImpl())
    } 
}
