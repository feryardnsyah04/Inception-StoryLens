// 1. Sesuaikan nama package
package com.inception.storylens.ui.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.core.view.WindowInsetsCompat
// 2. Sesuaikan import R dan ViewModel
import com.inception.storylens.R
import com.inception.storylens.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    navController: NavController,
    splashViewModel: SplashViewModel = viewModel()
) {
    val view = LocalView.current
    val window = (view.context as? android.app.Activity)?.window

    // Efek untuk menyembunyikan status bar (opsional, tapi bagus untuk splash screen)
    DisposableEffect(key1 = Unit) {
        if (window != null) {
            WindowCompat.getInsetsController(window, view).let { controller ->
                controller.isAppearanceLightStatusBars = false
                controller.hide(WindowInsetsCompat.Type.systemBars())
            }
        }
        onDispose {
            if (window != null) {
                WindowCompat.getInsetsController(window, view).let { controller ->
                    controller.isAppearanceLightStatusBars = true
                    controller.show(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
    }

    // Logika navigasi setelah delay dari ViewModel
    LaunchedEffect(key1 = Unit) {
        splashViewModel.navigationEvent.collect { route ->
            navController.navigate(route) {
                // 3. Ganti rute popUpTo menjadi "splash" sebagai String
                popUpTo("splash") {
                    inclusive = true
                }
            }
        }
    }

    // Tampilan UI: Hanya gambar fullscreen
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.loading_screen),
            contentDescription = "Splash Screen Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}