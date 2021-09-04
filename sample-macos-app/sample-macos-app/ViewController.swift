import Cocoa
import shared

class ViewController: NSViewController {

    @IBOutlet private weak var button: NSButton!
    @IBOutlet private weak var imageView: NSImageView!
    @IBOutlet private weak var progressIndicator: NSProgressIndicator!
    private let binder = KittenBinder(storeBuilder: KittenStoreBuilderImpl())
    private lazy var viewMediator = { KittenViewMediator(show: show) }()

    override func viewDidLoad() {
        super.viewDidLoad()

        binder.onViewCreated(view: viewMediator)
    }

    override func viewWillAppear() {
        super.viewWillAppear()
        
        binder.onStart()
    }
    
    override func viewDidDisappear() {
        binder.onStop()
        
        super.viewDidDisappear()
    }

    deinit {
        binder.onViewDestroyed()
        binder.onDestroy()
    }
    
    @IBAction func buttonClicked(button: NSButton) {
        viewMediator.dispatch(event: KittenViewEvent.Reload())
    }
    
    func show(model: KittenViewViewModel) {
        imageView.image = nil
        if (!model.isLoading && model.kittenUrl != nil) {
            progressIndicator.isHidden = false
            imageView.loadImageByUrl(url: model.kittenUrl!) { [weak self] in
                self?.progressIndicator.isHidden = true
            }
        } else {
            progressIndicator.isHidden = !model.isLoading
            imageView.cancelImageLoading()
        }
        
        if (model.isError) {
            viewMediator.dispatch(event: KittenViewEvent.ErrorShown())
            showError()
        }
    }
    
    private func showError() {
        let alert = NSAlert()
        alert.messageText = "Error loading kitten"
        alert.window.title = "Error"
        alert.alertStyle = NSAlert.Style.warning
        alert.runModal()
    }
}
