package ipca.example.topnews.ui.articles

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.swagger.client.models.Article
import ipca.example.topnews.R
import ipca.example.topnews.ui.theme.TopNewsTheme

@Composable
fun ArticleRow( article: Article,
                onItemClick : ()->Unit){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ){

        if (article.urlToImage == null) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "${article.title}",
                modifier = Modifier
                    .size(120.dp)
                    .padding(end = 8.dp)
            )
        }else{
            AsyncImage(
                model = article.urlToImage,
                contentDescription = "${article.title}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .padding(end = 8.dp)
            )
        }




        Column {
            Text(
                text = "${article.title}",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "${article.description}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "${article.publishedAt}",
                style = MaterialTheme.typography.labelSmall,
            )
        }


    }
}


@Preview(showBackground = true)
@Composable
fun  ArticleRowPreview() {
    TopNewsTheme {
        ArticleRow(
            Article(
                title = "Title1",
                description = "Description1",
                url = "Url1",
            ),
            onItemClick = {}
        )
    }
}