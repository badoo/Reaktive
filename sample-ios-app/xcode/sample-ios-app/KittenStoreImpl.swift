import Foundation
import ReactiveSample
import Alamofire

class KittenLoaderImpl: KittenLoader {
    
    struct KittenResponse: Codable {
        let id: String
        let url: String
    }
    
    func load() -> Single {
        return SingleByEmitterKt.single { (emitter: SingleEmitter) -> KotlinUnit in
            AF.request(Config.init().KITTEN_URL)
                .responseString { (response: DataResponse<String>) in
                    do {
                        try emitter.onSuccess(value: KittenLoaderResult.Success(json: response.result.get()))
                    } catch {
                        emitter.onSuccess(value: KittenLoaderResult.Error.init())
                    }
            }
            return KotlinUnit.init()
        }
    }
}
