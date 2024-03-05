package ipca.example.topnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ipca.example.topnews.ui.articles.ArticleDetail
import ipca.example.topnews.ui.articles.ArticleDetailViewModel
import ipca.example.topnews.ui.articles.ArticlesList
import ipca.example.topnews.ui.articles.ArticlesViewModel
import ipca.example.topnews.ui.navigation.BottomNavigationItem
import ipca.example.topnews.ui.navigation.Screen
import ipca.example.topnews.ui.theme.TopNewsTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TopNewsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    ScaffoldExample(navController = navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldExample( navController : NavHostController) {
    var title by remember { mutableStateOf("Home") }
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
            screen = Screen.Home
        ),
        BottomNavigationItem(
            title = "Technology",
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.Email,
            hasNews = false,
            badgeCount = 45,
            screen = Screen.Technology
        ),
        BottomNavigationItem(
            title = "Sports",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            hasNews = true,
            screen = Screen.Sports
        ),
    )
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis // Optional: Add ellipsis if text overflows
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
        bottomBar = {

            BottomAppBar  {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                title = item.title
                                if (item.screen is Screen.Sports) {
                                    navController.navigate(item.screen.route.replace("{category}", "sports"))
                                }
                                if (item.screen is Screen.Technology) {
                                    navController.navigate(item.screen.route.replace("{category}", "technology"))
                                }
                                if (item.screen is Screen.Home) {
                                    navController.navigate(item.screen.route.replace("{category}", "general"))
                                }
                            },
                            label = {
                                Text(text = item.title)
                            },
                            alwaysShowLabel = true,
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedItemIndex) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            NavHost(navController = navController,
                startDestination = Screen.Home.route

            ){
                navigation(startDestination = "Sports1",
                    route = "Sports2") {
                    composable(Screen.Sports.route) {
                        val viewModel = hiltViewModel<ArticlesViewModel>()
                        val state = viewModel.state.value

                        ArticlesList(
                            navController = navController,
                            state = state
                        )
                    }
                    composable(route = Screen.Article.route) {
                        val viewModel = hiltViewModel<ArticleDetailViewModel>()
                        val state = viewModel.state.value
                        val articleUrl = it.arguments?.getString("articleUrl")
                        val articleTitle = it.arguments?.getString("articleTitle")
                        title = articleTitle!!
                        state.url = articleUrl?.decodeURL()
                        ArticleDetail(navController = navController, state = state)
                    }
                }

                composable(
                    route = Screen.Technology.route
                ) {
                    val viewModel = hiltViewModel<ArticlesViewModel>()
                    val state = viewModel.state.value
                    ArticlesList(
                        navController = navController,
                        state = state
                    )
                }
                composable(
                    route = Screen.Home.route
                ) {
                    val viewModel = hiltViewModel<ArticlesViewModel>()
                    val state = viewModel.state.value
                    ArticlesList(
                        navController = navController,
                        state = state
                    )
                }
                composable(route = Screen.Article.route) {
                    val viewModel = hiltViewModel<ArticleDetailViewModel>()
                    val state = viewModel.state.value
                    val articleUrl = it.arguments?.getString("articleUrl")
                    val articleTitle = it.arguments?.getString("articleTitle")
                    title = articleTitle!!
                    state.url = articleUrl?.decodeURL()
                    ArticleDetail(navController = navController, state = state)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArticlesListPreview() {
    TopNewsTheme {
        ScaffoldExample (navController = rememberNavController())
    }
}