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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.inception.storylens.R

// Definisikan data untuk setiap item navigasi
data class BottomNavItem(
    val label: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val isSpecial: Boolean = false
)

@Composable
fun StoryLensBottomAppBar(
    selectedItemIndex: Int,
    onItemSelected: (Int) -> Unit,
    onAddClick: () -> Unit // Lambda khusus untuk tombol '+'
) {
    // Siapkan semua 5 item navigasi
    val navItems = listOf(
        BottomNavItem(
            label = "Beranda",
            selectedIcon = painterResource(id = R.drawable.ic_home_filled),
            unselectedIcon = painterResource(id = R.drawable.ic_home_outlined)
        ),
        BottomNavItem(
            label = "Jurnal",
            selectedIcon = painterResource(id = R.drawable.ic_journal_filled),
            unselectedIcon = painterResource(id = R.drawable.ic_journal_outlined)
        ),
        BottomNavItem(
            label = "Tambah",
            selectedIcon = painterResource(id = R.drawable.ic_add),
            unselectedIcon = painterResource(id = R.drawable.ic_add),
            isSpecial = true
        ),
        BottomNavItem(
            label = "Kalender",
            selectedIcon = painterResource(id = R.drawable.ic_calendar_filled),
            unselectedIcon = painterResource(id = R.drawable.ic_calendar_outlined)
        ),
        BottomNavItem(
            label = "Profil",
            selectedIcon = painterResource(id = R.drawable.ic_profile_filled),
            unselectedIcon = painterResource(id = R.drawable.ic_profile_outlined)
        )
    )

    // Gunakan NavigationBar
    NavigationBar(
        modifier = Modifier.clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        navItems.forEachIndexed { index, item ->
            // Gunakan index mapping yang benar karena item 'Tambah' tidak mempengaruhi state 'selected'
            // Item: Beranda(0), Jurnal(1), Tambah(2), Kalender(3), Profil(4)
            // selectedItemIndex: Beranda(0), Jurnal(1), Kalender(2), Profil(3)
            val isSelected = if (index < 2) {
                selectedItemIndex == index
            } else if (index > 2) {
                selectedItemIndex == index - 1
            } else {
                false
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (item.isSpecial) {
                        onAddClick()
                    } else {
                        // Map index klik ke index state
                        val newIndex = if (index < 2) index else index - 1
                        onItemSelected(newIndex)
                    }
                },
                icon = {
                    if (item.isSpecial) {
                        // Tampilan khusus untuk tombol '+'
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = item.selectedIcon,
                                contentDescription = item.label,
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                    } else if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = item.selectedIcon,
                                contentDescription = item.label,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        Icon(painter = item.unselectedIcon, contentDescription = item.label)
                    }
                },
                label = {
                    if (!isSelected && !item.isSpecial) { Text(item.label) }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}