//
//  Helpers.swift
//  TopNews
//
//  Created by Lourenço Gomes on 27/03/2021.
//

import Foundation
import CoreFoundation
import UIKit

func downloadImage(urlImageString: String , imageView : UIImageView?){
    
    if let url = URL(string: urlImageString) {

        let documentName = urlImageString.base64Encoded() ?? ""
        if let data : Data = CacheControl.getDataFromCache(documentName) {
            guard let image = UIImage(data: data) else { return }
            imageView?.image = image
        }else{
            URLSession.shared.dataTask(with: url) {
                (data, response, error) in
                guard let data = data,
                      let image = UIImage(data: data),
                      error == nil else {
                    return
                }
                CacheControl.sharedControl.pushData(toCache: data, identifier: documentName)
                DispatchQueue.main.sync() { () -> Void in
                    imageView?.image = image
                }
            }.resume()
        }
    }
}

extension Date {
    var toDateHourString : String {
        get{
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "yyyy-MM-dd HH:mm"
            return dateFormatter.string(from:self)
        }
    }
}

extension String {
    var parseDate : Date? {
        get{
            let formatter : DateFormatter = DateFormatter()
            formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'"
            return  formatter.date(from: self) as Date?
        }
    }
    
    //: ### Base64 encoding a string
    func base64Encoded() -> String? {
        if let data = self.data(using: .utf8) {
            return data.base64EncodedString()
        }
        return nil
    }
    
    //: ### Base64 decoding a string
    func base64Decoded() -> String? {
        if let data = Data(base64Encoded: self) {
            return String(data: data, encoding: .utf8)
        }
        return nil
    }
}


