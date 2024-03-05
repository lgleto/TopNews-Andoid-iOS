//
//  File.swift
//  TopNews
//
//  Created by Lourenço Gomes on 04/03/2024.
//

import Foundation
import SwaggerClient
import CoreData

struct ArticleState {
    var articles: Articles?
    var error : String?
    var isLoading : Bool = false
}

class ArticlesViewModel : ObservableObject {
    
    @Published var articleState = ArticleState()
    
    
    func getArticles(country : String, category : String, context: NSManagedObjectContext){
        self.articleState.isLoading = true
        Repository.getArticles(category: category, context: context) { (articles, error) in
            self.articleState.isLoading = false
            if (error == nil){
                self.articleState.articles = Articles(status: "ok", totalResults: articles.count, articles: articles)
            }else {
                self.articleState.error = error.debugDescription
                print(error.debugDescription)
            }
        }
        /*
        NewsAPI.topHeadlinesGet(apiKey: NEWS_API_KEY,
                                country: country,
                                category: category) { (articles, error) in
            self.articleState.isLoading = false
            if (error == nil){
                self.articleState.articles = articles
            }else {
                self.articleState.error = error.debugDescription
                print(error.debugDescription)
            }
        }*/
    }
    
    
}
