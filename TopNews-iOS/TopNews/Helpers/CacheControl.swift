//
//  CacheControl.swift
//  UN1Q.one
//
//  Created by Lourenço Gomes on 28/07/2020.
//  Copyright © 2020 Lourenço Gomes. All rights reserved.
//

import Foundation

let CACHE_DIR = "PhotoCache"
let MAX_CACHE = 1024*1024*100 // 100 Mb

class CacheControl {
    
    static let sharedControl : CacheControl = CacheControl()
    var identifiers: Set<String> = Set()
    var folderSize : Int {
        get{
            return storageSize()
        }
    }
    
    init() {
        let fileManager = FileManager.default
        let folderPath = CacheControl.folderPath()
        var files: [String]? = nil
        do {
            files = try fileManager.contentsOfDirectory(atPath: folderPath)
            if let files = files {
                identifiers = Set<String>(files)
            }
        } catch {
            
        }
    }
    
    func storageSize () -> Int{
        let fileManager = FileManager.default
        let folderPath = CacheControl.folderPath()
        var totalSize: Int = 0
        do {
            let arrayFiles = try fileManager.contentsOfDirectory(at: URL(fileURLWithPath: folderPath), includingPropertiesForKeys: nil)
            for strFilePath in arrayFiles {
                let fileDictionary = try? fileManager.attributesOfItem(atPath: strFilePath.path)
                let size = fileDictionary?[.size] as? Int
                totalSize = totalSize + (size ?? 0)
                if let aSize = size {
                    print("\(strFilePath.absoluteString)) - \(aSize)")
                }
            }
            
        } catch {
            print("Error while enumerating files \(folderPath): \(error.localizedDescription)")
        }
        return totalSize / (1024 * 1024)
    }
    
    func pushData(toCache data: Data, identifier: String) {
        let fileManager = FileManager()
        let folderPath = CacheControl.folderPath()
        let maxSize = MAX_CACHE
        let sharedControl = CacheControl.sharedControl
        if !fileManager.fileExists(atPath: folderPath) {
            do {
                try fileManager.createDirectory(atPath: folderPath, withIntermediateDirectories: false, attributes: nil)
            
            } catch {
                 print(error.localizedDescription)
            }
        }
        
        let fullFilePathString = "\(folderPath)/\(identifier)"
        if fileManager.createFile(atPath: fullFilePathString, contents: data, attributes: nil)
        {
            sharedControl.identifiers.insert(identifier)
            while (sharedControl.folderSize) >= maxSize {
                self.removeOldestFile()
            }
        }
        
    }
    
    func removeOldestFile() {
        let fileManager = FileManager()
        var filePaths: [String]? = nil
        do {
            filePaths = try fileManager.subpathsOfDirectory(atPath: CacheControl.folderPath())
            var oldestDate = Date()
            var oldestFile: String?
            for filePath in filePaths ?? [] {
                print("Checking \(filePath)")
                var fileDictionary: [FileAttributeKey : Any]? = nil
                do {
                    fileDictionary = try fileManager.attributesOfItem(atPath: "\(CacheControl.folderPath())/\(filePath)")
                    if (fileDictionary?[FileAttributeKey.modificationDate] as! Date).compare(oldestDate) == .orderedAscending {
                        if let file = fileDictionary?[FileAttributeKey.modificationDate] as? Date {
                            oldestDate = file
                        }
                        oldestFile = filePath
                    }
                    print("Deleting: \(oldestFile ?? "")")
                    self.removeIdentifierAndDeleteFile(oldestFile ?? "")
                } catch let attributesError {
                    print(attributesError)
                }
            }
        } catch let filePathsError {
            print(filePathsError)
        }
    }
    
    static func folderPath() -> String {
        let paths = NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)
        let documentsDirectory = paths[0]
        let docURL = URL(string: documentsDirectory)!
        let dataPath = docURL.appendingPathComponent(CACHE_DIR)
        return dataPath.absoluteString
    }
    
    static func getDataFromCache(_ identifier: String) -> Data? {
        let fileManager = FileManager()
        let data = fileManager.contents(atPath: "\(CacheControl.folderPath())/\(identifier)")
        return data
    }
    
    func getFileString(fromCache identifier: String) -> String {
        return "\(CacheControl.folderPath())/\(identifier)"
    }
    
    func containsIdentifier(_ identifier: String?) -> Bool {
        return CacheControl.sharedControl.identifiers.contains(identifier ?? "")
    }
    
    func removeIdentifierAndDeleteFile(_ identifier: String) {
        let fileManager = FileManager()
        let filePath = "\(CacheControl.folderPath())/\(identifier)"
        do {
            try fileManager.removeItem(atPath: filePath)
            CacheControl.sharedControl.identifiers.remove(identifier)
        } catch {
        }
    }
}

extension Set {
    
    @discardableResult mutating func insert(_ newMembers: [Set.Element]) -> [(inserted: Bool, memberAfterInsert: Set.Element)] {
        var returnArray: [(inserted: Bool, memberAfterInsert: Set.Element)] = []
        newMembers.forEach { (member) in
            returnArray.append(self.insert(member))
        }
        return returnArray
    }
}
