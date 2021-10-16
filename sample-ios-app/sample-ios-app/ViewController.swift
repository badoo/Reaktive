import UIKit
import shared
import AlamofireImage

class ViewController: UIViewController {
    
    @IBOutlet weak var indicator: UIActivityIndicatorView!
    @IBOutlet weak var button: UIButton!
    @IBOutlet weak var image: UIImageView!
    
    private let kittenBinder = KittenBinder(storeBuilder: KittenStoreBuilderImpl())

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(_: animated)
        kittenBinder.onViewCreated(view: KittenView(viewController: self))
        kittenBinder.onStart()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        kittenBinder.onStop()
        kittenBinder.onViewDestroyed()
        super.viewWillDisappear(_: animated)
    }

    deinit {
        kittenBinder.onDestroy()
    }
    
    private class KittenView: AbstractKittenView {
        private let viewController: ViewController
        
        init(viewController: ViewController) {
            self.viewController = viewController
            super.init()
            viewController.button.addTarget(self, action: #selector(onClick), for: .touchUpInside)
        }
        
        @objc func onClick() {
            dispatch(event: KittenViewEvent.Reload.init())
        }
        
        override func show(model: KittenViewViewModel) {
            viewController.indicator.show(isShow: model.isLoading)
            if (!model.isLoading && model.kittenUrl != nil) {
                showImage(model: model)
            } else {
                hideImage()
            }
            if (model.isError) {
                showError()
            }
        }
        
        private func showImage(model: KittenViewViewModel) {
            viewController.image.isHidden = false
            viewController.image.image = nil
            viewController.indicator.isHidden = false
            viewController.image!.af.setImage(
                withURL: URL(string: model.kittenUrl!)!,
                completion: { response in
                    self.viewController.indicator.isHidden = response.data != nil
                }
            )
        }
        
        private func hideImage() {
            viewController.image.isHidden = true
            viewController.image.af.cancelImageRequest()
        }
        
        private func showError() {
            let alertController = UIAlertController(title: "Error", message: "Failed to download kitten", preferredStyle: .alert)
            alertController.addAction(UIAlertAction(title: "OK", style: .default))
            viewController.present(alertController, animated: true, completion: {
                self.dispatch(event: KittenViewEvent.ErrorShown.init())
            })
        }
    }
}

extension UIActivityIndicatorView {

    func show(isShow: Bool) {
        self.isHidden = !isShow
        if (isShow) {
            self.startAnimating()
        } else {
            self.stopAnimating()
        }
    }

}

