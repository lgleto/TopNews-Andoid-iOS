//
//  NewsVC.swift
//  TopNews
//
//  Created by Lourenço Gomes on 27/03/2021.
//

import UIKit
import WebKit
import SwaggerClient

class ArticleDetailVC: UIViewController {

    var article : Article?
    
    @IBOutlet weak var webView: WKWebView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if let url = URL(string: article!.url!) {
            webView.load(URLRequest.init(url: url))
        }
        self.title = article?.title
    
    }
    
 
    @IBAction func actionShare(_ sender: Any) {
        let dataToShare = ["TopNews",
                           article?.url,
                           article?.title, nil]
    
        let activityViewController = UIActivityViewController(activityItems: dataToShare as [Any], applicationActivities: nil)
        activityViewController.setValue("TopNews", forKey: "subject")
        activityViewController.excludedActivityTypes = [.saveToCameraRoll,
                                                        .assignToContact,
                                                        .print]
        activityViewController.popoverPresentationController?.sourceView = (sender as! UIBarButtonItem).customView
        present(activityViewController, animated: true, completion: nil)
        

    }
}
