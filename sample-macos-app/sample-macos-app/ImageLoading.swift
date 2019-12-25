import Cocoa

public extension NSImageView {
    private var activeTask: URLSessionDataTask? {
        get {
            return objc_getAssociatedObject(self, &AssociatedKey.task) as? URLSessionDataTask
        }
        set {
            objc_setAssociatedObject(self, &AssociatedKey.task, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
   
    func loadImageByUrl(url: String, completion: @escaping () -> Void) {
        cancelImageLoading()

        let task = URLSession.shared.dataTask(with: URL(string: url)!) { [weak self] data, d, error in
            guard let data = data, error == nil else { return }
            DispatchQueue.main.async {
                self?.image = NSImage(data: data)
                completion()
            }
        }
        activeTask = task
        task.resume()
    }
    
    func cancelImageLoading() {
        guard let task = activeTask else { return }
        activeTask = nil
        task.cancel()
    }
    
    private struct AssociatedKey {
        static var task = "task"
    }
}
