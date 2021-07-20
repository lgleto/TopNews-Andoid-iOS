//
//  NewsTVC.swift
//  TopNews
//
//  Created by Lourenço Gomes on 26/03/2021.
//

import UIKit
import SwaggerClient
import CoreData
import Alamofire

class ArticlesTVC: UITableViewController {
    
    var articles : [Article] = []
    var category : String = ""
    
    private var managedObjectContext: NSManagedObjectContext? {
        didSet {
            if let context = managedObjectContext {
                 Repository.getArticles(category: self.category, context: context, callback: { articles, error in
                    if error == nil {
                        self.articles = articles.sorted(by: { a1, a2 in
                            a1.publishedAt?.compare(a2.publishedAt ?? "") == .orderedDescending
                        })
                        self.tableView.reloadData()
                    }
                })
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.managedObjectContext = (UIApplication.shared.delegate as? AppDelegate)?.persistentContainer.viewContext
    }
    
    // MARK: - Table view data source
    
    override func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return articles.count
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell : ArticleTVCell = tableView.dequeueReusableCell(withIdentifier: "ArticleCell", for: indexPath) as! ArticleTVCell
        
        cell.title?.text = articles[indexPath.row].title
        cell.smallDescription?.text = articles[indexPath.row]._description
        cell.imageArticle?.image = UIImage(systemName: "newspaper")
        cell.pubDate.text = articles[indexPath.row].publishedAt?.parseDate?.toDateHourString
        downloadImage(urlImageString: articles[indexPath.row].urlToImage ?? "", imageView: cell.imageArticle)
        
        return cell
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if (NetworkReachabilityManager.init()?.isReachable) ?? false{
            performSegue(withIdentifier: "webArticleSegue", sender: self)
        }else{
            performSegue(withIdentifier: "offlineArticleSegue", sender: self)
        }
    }
    
    // MARK: - Navigation
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let vc  : ArticleDetailVC = segue.destination as? ArticleDetailVC {
            if let indexPath = self.tableView.indexPathForSelectedRow {
                vc.article = articles[indexPath.row]
            }
        }
        if let vc  : ArticleOfflineDetailVC = segue.destination as? ArticleOfflineDetailVC {
            if let indexPath = self.tableView.indexPathForSelectedRow {
                vc.article = articles[indexPath.row]
            }
        }
    }
    
}
