//
//  ArticlesCachedView.swift
//  TopNews
//
//  Created by Lourenço Gomes on 05/03/2024.
//

import SwiftUI
import SwaggerClient
import CachedAsyncImage

struct ArticlesCachedView: View {
    var country : String
    var category : String
    @Environment(\.managedObjectContext) private var viewContext
    @ObservedObject var viewModel : ArticlesViewModel = ArticlesViewModel()
    
    @FetchRequest(
        sortDescriptors: [NSSortDescriptor(keyPath: \ArticleCache.url, ascending: true)],
        animation: .default)
    private var articlesCache: FetchedResults<ArticleCache>

    var body: some View {
        NavigationView {
            List {
                ForEach(articlesCache) { articleCache in
                    let article = Article.fromJson(jsonString: articleCache.jsonString!)
                    NavigationLink(destination:
                                    ArticleDetailView(
                                        url:article.url ?? "",
                                        title: article.title ?? "")
                    ){
                        ArticleRowView(article: article)
                    }
                }
            }.onAppear{
                viewModel.getArticles(country: country, category: category, context: viewContext)
            }.navigationBarTitle(category.capitalized)
        }
    }
}

#Preview {
    ArticlesCachedView(country: "us", category: "sports")
        .environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)

}



