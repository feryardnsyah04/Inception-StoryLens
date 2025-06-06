package com.inception.storylens.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.inception.storylens.viewmodel.AuthViewModel

data class JournalEntry(
    val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val timestamp: Long = 0L
)

@Composable
fun HomeScreen(navController: NavHostController, viewModel: AuthViewModel) {

}

@Preview(showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HomeScreenPreviewLight() {
    //HomeScreen(navController, authViewModel)
}

@Preview(showSystemUi = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreviewDark() {
    //HomeScreen(navController, authViewModel)
}
