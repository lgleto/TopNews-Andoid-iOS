//
//  SwiftUIView.swift
//  TopNews
//
//  Created by Lourenço Gomes on 04/03/2024.
//

import SwiftUI
import SwaggerClient
import CachedAsyncImage

struct ArticlesView: View {
    var country : String
    var category : String
    @Environment(\.managedObjectContext) private var viewContext
    @ObservedObject var viewModel : ArticlesViewModel = ArticlesViewModel()

    var body: some View {
        NavigationView {
            List {
                ForEach(viewModel.articleState.articles?.articles ?? [Article]()) { article in
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

struct ArticleRowView : View {
    
    var article : Article
    
    var body: some View {
        HStack{
            CachedAsyncImage(url: URL(string:article.urlToImage ?? "")) { phase in
                  switch phase {
                  case .success(let image):
                      image
                          .resizable()
                          .aspectRatio(contentMode: .fill)
                          .frame(width: 100, height: 100)
                          .clipped()
                  case .failure(_):
                      Text("Failed to load image")
                  case .empty:
                      Image(systemName: "newspaper")
                          .resizable()
                          .frame(width: 100, height: 100)
                  @unknown default:
                      Text("Loading...")
                  }
              }

            VStack{
                Text(article.title ?? "")
                    .lineLimit(1)
                    .padding(EdgeInsets(top: 0, leading: 0, bottom: 1, trailing: 0))
                    .bold()
                Text(article.content ?? "")
                    .lineLimit(3)
            }

        }
    }
}

#Preview {
    let articlesView = ArticlesView(country: "us", category: "sports")
    return articlesView
}

extension Article : Identifiable {
    public var id: String {UUID().uuidString}
}
