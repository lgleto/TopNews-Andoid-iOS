package ipca.example.topnews.ui.articles

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.swagger.client.models.Article
import io.swagger.client.models.Articles

import ipca.example.topnews.encodeURL
import ipca.example.topnews.ui.navigation.Screen
import ipca.example.topnews.ui.theme.TopNewsTheme


@Composable
    fun ArticlesList(
        navController: NavController,
        state : ArticlesState
    ) {
    Box(modifier = Modifier.fillMaxSize()){
        if(state.error.isNotBlank()){
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        }else if(state.isLoading){
            Text(
                text = "Loading Articles...",
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        }else {
            LazyColumn(
                contentPadding = PaddingValues(all = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(
                    items = state.articles?.articles ?: arrayOf(),
                    key = { index, article ->
                        article.url!!
                    }
                ) { index, article ->
                    Log.d("MainActivity", index.toString())
                    ArticleRow(
                        article = article,
                        onItemClick = {
                            navController.navigate(
                                Screen.Article.route
                                    .replace("{articleUrl}", article.url!!.encodeURL())
                                    .replace("{articleTitle}", article.title!!)
                            )
                        }
                    )
                }
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun ArticlesListPreview() {
    TopNewsTheme {
        ArticlesList(
            rememberNavController(),
            ArticlesState(
                articles = Articles(
                    status = "",
                    totalResults = 0,
                    articles = NewsRepository.getAllData
                  )  ,
                error = "",
                isLoading = false

            )
        )
    }
}

object NewsRepository {

    var getAllData: Array<Article> =
        arrayOf(
            Article(
                title = "Title1",
                description = "Description1",
                url = "Url1"
            ),
            Article(
                title = "Title2",
                description = "Description2",
                url = "Url2"
            ),
            Article(
                title = "Title3",
                description = "Description3",
                url = "Url3"
            )
        )
}