import Foundation
import ReactiveSample
import Alamofire

class KittenDataSourceImpl: KittenDataSource {
    func load(url: String, continuation: @escaping (String?, KotlinThrowable?) -> KotlinUnit) {
        AF.request(url)
            .responseString { (response: DataResponse<String>) in
                do {
                    try continuation(response.result.get(), nil)
                } catch {
                    continuation(nil, KotlinRuntimeException(message: error.localizedDescription))
                }
        }
    }
}
