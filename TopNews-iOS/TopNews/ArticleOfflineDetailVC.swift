//
//  ArticleOfflineDetail.swift
//  TopNews
//
//  Created by Lourenço Gomes on 28/04/2021.
//

import UIKit
import SwaggerClient

class ArticleOfflineDetailVC: UIViewController {

    var article : Article?
    
    @IBOutlet weak var labelTitle: UILabel!
    @IBOutlet weak var labelDescription: UILabel!
    @IBOutlet weak var imageArticle: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        labelTitle.text = article?.title
        labelDescription.text = article?._description
        downloadImage(urlImageString: article?.urlToImage ?? "", imageView: imageArticle)
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
