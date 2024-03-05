package ipca.example.topnews.ui.articles

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import ipca.example.topnews.ui.theme.TopNewsTheme

@Composable
fun ArticleDetail(
    navController: NavController,
    state : ArticleState
) {
    Box(modifier = Modifier.fillMaxSize()){
        WebViewScreen(state.url!!)
    }
}

@Composable
fun WebViewScreen(urlString : String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = WebViewClient()
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)
            }
        },
        update = { webView ->
            webView.loadUrl(urlString)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun WebViewScreenPreview() {
    TopNewsTheme {
        Box(modifier = Modifier.fillMaxSize()){
            WebViewScreen("https://www.ldoceonline.com/")
        }
    }
}