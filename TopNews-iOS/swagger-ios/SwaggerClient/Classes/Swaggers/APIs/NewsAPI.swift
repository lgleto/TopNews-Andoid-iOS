//
// NewsAPI.swift
//
// Generated by swagger-codegen
// https://github.com/swagger-api/swagger-codegen
//

import Foundation
import Alamofire



open class NewsAPI {
    /**

     - parameter apiKey: (query)  
     - parameter country: (query)  (optional)
     - parameter category: (query)  (optional)
     - parameter completion: completion handler to receive the data and the error objects
     */
    open class func topHeadlinesGet(apiKey: String, country: String? = nil, category: String? = nil, completion: @escaping ((_ data: Articles?,_ error: Error?) -> Void)) {
        topHeadlinesGetWithRequestBuilder(apiKey: apiKey, country: country, category: category).execute { (response, error) -> Void in
            completion(response?.body, error)
        }
    }


    /**
     - GET /top-headlines
     - Get user playlists
     - API Key:
       - type: apiKey apiKey (QUERY)
       - name: APIKeyQueryParam
     - examples: [{contentType=application/json, example={
  "totalResults" : 0,
  "articles" : [ {
    "publishedAt" : "publishedAt",
    "author" : "author",
    "urlToImage" : "urlToImage",
    "description" : "description",
    "title" : "title",
    "user" : {
      "name" : "name",
      "id" : "id"
    },
    "url" : "url",
    "content" : "content"
  }, {
    "publishedAt" : "publishedAt",
    "author" : "author",
    "urlToImage" : "urlToImage",
    "description" : "description",
    "title" : "title",
    "user" : {
      "name" : "name",
      "id" : "id"
    },
    "url" : "url",
    "content" : "content"
  } ],
  "status" : "status"
}}]
     
     - parameter apiKey: (query)  
     - parameter country: (query)  (optional)
     - parameter category: (query)  (optional)

     - returns: RequestBuilder<Articles> 
     */
    open class func topHeadlinesGetWithRequestBuilder(apiKey: String, country: String? = nil, category: String? = nil) -> RequestBuilder<Articles> {
        let path = "/top-headlines"
        let URLString = SwaggerClientAPI.basePath + path
        let parameters: [String:Any]? = nil
        
        var url = URLComponents(string: URLString)
        url?.queryItems = APIHelper.mapValuesToQueryItems([
            "country": country, 
            "category": category, 
            "apiKey": apiKey
        ])

        let requestBuilder: RequestBuilder<Articles>.Type = SwaggerClientAPI.requestBuilderFactory.getBuilder()

        return requestBuilder.init(method: "GET", URLString: (url?.string ?? URLString), parameters: parameters, isBody: false)
    }

}
