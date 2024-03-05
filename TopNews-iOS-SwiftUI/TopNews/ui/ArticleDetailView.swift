//
//  ArticleDetailView.swift
//  TopNews
//
//  Created by Lourenço Gomes on 04/03/2024.
//

import SwiftUI
import WebKit

struct ArticleDetailView: View {
    
    var url : String
    var title : String
    
    var body: some View {
        WebView(urlString: url).navigationTitle(title)
    }
}

#Preview {
    ArticleDetailView(url: "https://google.com", title: "Google")
}

struct WebView: UIViewRepresentable {
 
    let webView: WKWebView
    var urlString : String
    
    init(urlString : String) {
        webView = WKWebView(frame: .zero)
        self.urlString = urlString
    }
    
    func makeUIView(context: Context) -> WKWebView {
        return webView
    }
    func updateUIView(_ uiView: WKWebView, context: Context) {
        webView.load(URLRequest(url: URL(string: urlString)!))
    }
}
