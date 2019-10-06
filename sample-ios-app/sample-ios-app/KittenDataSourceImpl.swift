import Foundation
import sample_mpp_module
import Alamofire

class KittenDataSourceImpl: KittenDataSource {
    func load(url: String, continuation: @escaping (KittenDataSourceResult) -> Void) {
        AF.request(url)
            .responseString { (response: AFDataResponse<String>) in
                do {
                    try continuation(KittenDataSourceResult.Success(data: response.result.get()))
                } catch {
                    continuation(KittenDataSourceResult.Failure(throwable: KotlinRuntimeException(message: error.localizedDescription)))
                }
        }
    }
}
