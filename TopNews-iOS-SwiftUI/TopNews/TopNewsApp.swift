//
//  TopNewsApp.swift
//  TopNews
//
//  Created by Lourenço Gomes on 04/03/2024.
//

import SwiftUI

@main
struct TopNewsApp: App {
    let persistenceController = PersistenceController.shared

    var body: some Scene {
        WindowGroup {
            ContentView()
                .environment(\.managedObjectContext, persistenceController.container.viewContext)
        }
    }
}
