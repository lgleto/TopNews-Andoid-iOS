//
//  ContentView.swift
//  TopNews
//
//  Created by Lourenço Gomes on 04/03/2024.
//

import SwiftUI
import CoreData
import SwaggerClient

struct ContentView: View {
    @Environment(\.managedObjectContext) private var viewContext

    @FetchRequest(
        sortDescriptors: [NSSortDescriptor(keyPath: \Item.timestamp, ascending: true)],
        animation: .default)
    private var items: FetchedResults<Item>

    var body: some View {
        TabView {
            ArticlesView(country: COUNTRY_US, category: ENDPOINT_GENERAL)
                .tabItem {
                    Label("General", systemImage: "house")
                }
                .environment(\.managedObjectContext, viewContext)
            ArticlesView(country: COUNTRY_US, category: ENDPOINT_SPORTS)
                .tabItem {
                    Label("Sports", systemImage: "figure.run")
                }
                .environment(\.managedObjectContext, viewContext)
            ArticlesCachedView(country: COUNTRY_US, category: ENDPOINT_SCIENCE)
                .tabItem {
                    Label("Science", systemImage: "atom")
                }
                .environment(\.managedObjectContext, viewContext)
        }
    }
}


#Preview {
    ContentView().environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
}
