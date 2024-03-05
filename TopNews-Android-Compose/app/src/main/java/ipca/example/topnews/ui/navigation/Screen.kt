package ipca.example.topnews.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home/{category}")
    object Technology : Screen("technology/{category}")
    object Sports : Screen("sports/{category}")
    object Article : Screen("article/{articleUrl}/{articleTitle}")
}
