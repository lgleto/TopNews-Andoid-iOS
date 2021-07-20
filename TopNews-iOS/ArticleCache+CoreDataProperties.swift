//
//  ArticleCache+CoreDataProperties.swift
//  TopNews
//
//  Created by Lourenço Gomes on 28/04/2021.
//
//

import Foundation
import CoreData


extension ArticleCache {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<ArticleCache> {
        return NSFetchRequest<ArticleCache>(entityName: "ArticleCache")
    }

    @NSManaged public var url: String?
    @NSManaged public var jsonString: String?
    @NSManaged public var category: String?

}

extension ArticleCache : Identifiable {

}
