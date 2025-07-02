package com.inception.storylens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.inception.storylens.R

// 1. Modifikasi data class untuk menyertakan 'route'
data class BottomNavItem(
    val label: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val route: String, // Rute tujuan navigasi
    val isSpecial: Boolean = false
)

@Composable
// 2. Ubah parameter untuk menerima NavController
fun StoryLensBottomAppBar(
    navController: NavController,
    onAddClick: () -> Unit // Lambda untuk tombol '+' tetap sama
) {
    // Siapkan semua item navigasi dengan rutenya masing-masing
    val navItems = listOf(
        BottomNavItem(
            label = "Beranda",
            selectedIcon = painterResource(id = R.drawable.ic_home_filled),
            unselectedIcon = painterResource(id = R.drawable.ic_home_outlined),
            route = "home"
        ),
        BottomNavItem(
            label = "Jurnal",
            selectedIcon = painterResource(id = R.drawable.ic_journal_filled),
            unselectedIcon = painterResource(id = R.drawable.ic_journal_outlined),
            route = "journal"
        ),
        BottomNavItem(
            label = "Tambah",
            selectedIcon = painterResource(id = R.drawable.ic_add),
            unselectedIcon = painterResource(id = R.drawable.ic_add),
            route = "add_journal", // Rute ini akan ditangani oleh onAddClick
            isSpecial = true
        ),
        BottomNavItem(
            label = "Kalender",
            selectedIcon = painterResource(id = R.drawable.ic_calendar_filled),
            unselectedIcon = painterResource(id = R.drawable.ic_calendar_outlined),
            route = "calendar"
        ),
        BottomNavItem(
            label = "Profil",
            selectedIcon = painterResource(id = R.drawable.ic_profile_filled),
            unselectedIcon = painterResource(id = R.drawable.ic_profile_outlined),
            route = "profile"
        )
    )

    // Dapatkan rute saat ini dari NavController untuk menentukan item mana yang aktif
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = Modifier.clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        navItems.forEach { item ->
            // 3. Logika isSelected sekarang lebih sederhana dan aman
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                // 4. Logika onClick sekarang lebih sederhana
                onClick = {
                    if (item.isSpecial) {
                        onAddClick()
                    } else {
                        navController.navigate(item.route) {
                            // Pop up ke start destination untuk menghindari tumpukan navigasi
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Hindari membuat ulang destinasi yang sama
                            launchSingleTop = true
                            // Kembalikan state saat menavigasi kembali
                            restoreState = true
                        }
                    }
                },
                icon = {
                    val iconPainter = if (isSelected) item.selectedIcon else item.unselectedIcon

                    if (item.isSpecial) {
                        // Tampilan khusus untuk tombol '+'
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painter = iconPainter, contentDescription = item.label, tint = MaterialTheme.colorScheme.surface)
                        }
                    } else if (isSelected) {
                        // Tampilan khusus untuk item yang terpilih
                        Box(
                            modifier = Modifier.size(44.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(painter = iconPainter, contentDescription = item.label, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                    } else {
                        // Tampilan item biasa (tidak terpilih)
                        Icon(painter = iconPainter, contentDescription = item.label)
                    }
                },
                label = {
                    // Hanya tampilkan label jika tidak terpilih dan bukan tombol spesial
                    if (!isSelected && !item.isSpecial) {
                        Text(item.label)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color.Transparent // Hilangkan indikator default
                )
            )
        }
    }
}