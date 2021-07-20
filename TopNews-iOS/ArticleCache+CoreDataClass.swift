//
//  ArticleCache+CoreDataClass.swift
//  TopNews
//
//  Created by Lourenço Gomes on 28/04/2021.
//
//

import Foundation
import CoreData
import SwaggerClient

@objc(ArticleCache)
public class ArticleCache: NSManagedObject {
    
    
    class func addItem(url: String, jsonString: String, category: String, inManagedObjectContext  context:NSManagedObjectContext) ->ArticleCache?{
        
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: "ArticleCache")
        request.predicate = NSPredicate(format: "url = %@", url)
        
        if let _ = (try? context.fetch(request))?.first as? ArticleCache {
            return updateItem(url: url, jsonString: jsonString, category: category, inManagedObjectContext: context)
        }
        else if let articleCache = NSEntityDescription.insertNewObject(forEntityName: "ArticleCache", into: context) as? ArticleCache {
            articleCache.url        = url
            articleCache.jsonString = jsonString
            articleCache.category   = category
            return articleCache
        }
        return nil
    }
    
    class func updateItem(url: String, jsonString: String, category: String, inManagedObjectContext  context:NSManagedObjectContext) ->ArticleCache?{
        
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: "ArticleCache")
        request.predicate = NSPredicate(format: "url = %@", url)
        
        if let articleCache = (try? context.fetch(request))?.first as? ArticleCache {
            articleCache.url        = url
            articleCache.jsonString = jsonString
            articleCache.category   = category
            return articleCache
        }
        
        return nil
    }
    
    class func getAll(category: String, inManagedObjectContext  context:NSManagedObjectContext) -> [ArticleCache]?  {
        
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: "ArticleCache")
        request.predicate = NSPredicate(format: "category = %@", category )
        if let sessionCaches = (try? context.fetch(request)) as? [ArticleCache] {
            return sessionCaches
        }
        
        return nil
    }
    
    class func getItem(url: String, inManagedObjectContext  context:NSManagedObjectContext) -> ArticleCache?  {
        
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: "ArticleCache")
        request.predicate = NSPredicate(format: "url = %@", url)
        if let sessionCaches = (try? context.fetch(request)) as? [ArticleCache] {
            return sessionCaches.first
        }
        
        return nil
    }
    

    class func removeItem(url:String , inManagedObjectContext  context:NSManagedObjectContext) ->Bool{
        
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: "ArticleCache")
        request.predicate = NSPredicate(format: "url = %@", url)
        if let sessionCache = (try? context.fetch(request))?.first as? ArticleCache {
            context.delete(sessionCache)
            return true
        }
        
        return false
    }
    
    class func removeAllItems( inManagedObjectContext  context:NSManagedObjectContext) {
        let request = NSFetchRequest<NSFetchRequestResult>(entityName: "ArticleCache")
        if let objects = (try? context.fetch(request)) as? [ArticleCache] {
            for object in objects {
                context.delete(object)
            }
            do {
                try context.save()
            }catch{
                print("Error saving context")
            }
        }
    }

}

extension Article {
  
    func toJsonString() -> String {
        let jsonData = try! JSONEncoder().encode(self)
        let jsonString = String(data: jsonData, encoding: .utf8)!
        print(jsonString)
        return jsonString
    }
    
    static func fromJson(jsonString:String) -> Article {
        let jsonData = jsonString.data(using: .utf8)!
        let tester = try! JSONDecoder().decode(self, from: jsonData)
        return tester
    }
}
