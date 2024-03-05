//
//  ContentView.swift
//  TopNewsWatch Watch App
//
//  Created by Lourenço Gomes on 14/02/2024.
//

import SwiftUI
import SwaggerClient

struct ContentView: View {
    @State private var articles: [Article] = []

       var body: some View {
           NavigationView {
               List {
                   ForEach(articles, id: \.url) { item in
                       NavigationLink(destination: DetailView(item: item)) {
                           Text(item.title ?? "")
                       }
                   }
                   .onAppear{
                       loadData()
                   }
               }
           }
       }
    
    
    func loadData(){
        NewsAPI.topHeadlinesGet(apiKey: NEWS_API_KEY,
                                country: COUNTRY,
                                category: "sports") { (articles, error) in
            if (error == nil){
                if let arts = articles?.articles {
                    self.articles = arts
                }
            }else {

                print(error.debugDescription)
            }
        }
    }
}

struct DetailView: View {
    let item: Article

    var body: some View {
        Text(item.title ?? "")
            .navigationTitle("Detail")
    }
}


#Preview {
    ContentView()
}
