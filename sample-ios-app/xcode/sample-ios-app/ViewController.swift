import UIKit
import ReactiveSample

class ViewController: UIViewController {
    
    @IBOutlet weak var label: UILabel!
    @IBOutlet weak var indicator: UIActivityIndicatorView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        showLoading()
        ComputationKt.calculate().subscribe(observer: ResultObserver(showResult: showResult(text:)))
    }
    
    private func showLoading() {
        label.isHidden = true
        indicator.isHidden = false
    }
    
    private func showResult(text: String) {
        label.isHidden = false
        label.text = text
        label.sizeToFit()
        indicator.isHidden = true
    }
    
    class ResultObserver: SingleObserver {
        
        let showResult: (String) -> Void
        
        init(showResult: @escaping (String) -> Void) {
            self.showResult = showResult
        }
        
        func onSubscribe(disposable: Disposable) {
            // no-op
        }
        
        func onSuccess(value: Any?) {
            showResult("\(value ?? "no value")")
        }
        
        func onError(error: KotlinThrowable) {
            // no-op
            print(error)
        }
    }
}

