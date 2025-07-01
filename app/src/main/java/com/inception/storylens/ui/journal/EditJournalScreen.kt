package com.inception.storylens.ui.journal

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults // Import ini
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Import ini
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.inception.storylens.R
// Import warna kustom dari Color.kt Anda
import com.inception.storylens.ui.theme.LightSteelBlue
import com.inception.storylens.ui.theme.RoyalBlue
import com.inception.storylens.viewmodel.JournalUiState
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditJournalScreen(
    journalState: JournalUiState,
    navController: NavController,
    onUpdate: (title: String, note: String, imageUri: Uri?) -> Unit
) {
    val entryToEdit = journalState.selectedJournal

    var title by remember(entryToEdit) { mutableStateOf(entryToEdit?.title ?: "") }
    var notes by remember(entryToEdit) { mutableStateOf(entryToEdit?.note ?: "") }
    var imageUri by remember(entryToEdit) {
        mutableStateOf(entryToEdit?.imageUrl?.takeIf { it.isNotBlank() }?.toUri())
    }

    val context = LocalContext.current

    val tempCameraImageUri: Uri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            File(context.externalCacheDir, "temp_camera_image.jpg")
        )
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> if (uri != null) imageUri = uri }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success: Boolean ->
            if (success) {
                imageUri = tempCameraImageUri
            }
        }
    )

    Scaffold(
        // Menggunakan warna background dari MaterialTheme (LightSkyBlue di LightColorScheme Anda)
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (entryToEdit == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp) // Padding horizontal untuk konten utama
                    .verticalScroll(rememberScrollState()) // Scrollable content
                    .padding(bottom = 16.dp), // Padding bawah agar konten tidak terpotong
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TopAppBar (dengan warna transparan/menyesuaikan background)
                TopAppBar(
                    title = {
                        Text(
                            "Ubah Jurnal", // Judul TopAppBar untuk Edit Journal
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 6.dp),
                            color = RoyalBlue // Warna teks judul TopAppBar sesuai desain
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (navController.previousBackStackEntry != null) {
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Kembali",
                                tint = RoyalBlue // Warna ikon panah kembali sesuai desain
                            )
                        }
                    },
                    actions = {
                        Button(
                            onClick = {
                                onUpdate(title, notes, imageUri)
                            },
                            enabled = title.isNotBlank() && imageUri != null,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary, // MidnightBlue
                                contentColor = MaterialTheme.colorScheme.onPrimary // White
                            ),
                            shape = RoundedCornerShape(50)
                        ) { Text("Simpan") }
                    },
                    // Set colors TopAppBar menjadi transparan atau sesuai background
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // Membuat TopAppBar transparan
                        titleContentColor = RoyalBlue, // Warna teks judul
                        navigationIconContentColor = RoyalBlue, // Warna ikon navigasi
                        actionIconContentColor = RoyalBlue // Warna ikon aksi
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 1.dp, // bisa diatur sesuai selera
                    color = MaterialTheme.colorScheme.surface // Warna surface card/box (GhostWhite)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp))
                                // Ganti warna border gambar placeholder ke warna secondary dari tema (CornflowerBlue)
                                .border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUri != null) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Gambar Jurnal",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_image_placeholder),
                                    contentDescription = "Gambar Placeholder",
                                    // Ganti warna tint ikon placeholder gambar ke warna secondary dari tema (CornflowerBlue)
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { cameraLauncher.launch(tempCameraImageUri) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary, // MidnightBlue
                                    contentColor = MaterialTheme.colorScheme.onPrimary // White
                                ),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Menggunakan painterResource untuk ic_camera
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_camera),
                                        contentDescription = "Ikon Kamera",
                                        tint = MaterialTheme.colorScheme.onPrimary, // White
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text("Ambil Foto", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            Button(
                                onClick = { galleryLauncher.launch("image/*") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary, // MidnightBlue
                                    contentColor = MaterialTheme.colorScheme.onPrimary // White
                                ),
                                shape = RoundedCornerShape(50),
                                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Menggunakan painterResource untuk ic_gallery
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_gallery),
                                        contentDescription = "Ikon Galeri",
                                        tint = MaterialTheme.colorScheme.onPrimary, // White
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text("Dari Galeri", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
                Column {
                    Text("Judul", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Tambahkan judul....") },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            // Menggunakan LightSteelBlue untuk background TextField
                            focusedContainerColor = LightSteelBlue,
                            unfocusedContainerColor = LightSteelBlue,
                            disabledContainerColor = LightSteelBlue,
                            errorContainerColor = LightSteelBlue,
                            // Atur warna teks placeholder agar terlihat di atas LightSteelBlue
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            errorPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            // Atur warna teks input
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            errorTextColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
                Column {
                    Text("Catatan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp))
                    TextField(
                        value = notes,
                        onValueChange = { notes = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = { Text("Tambahkan catatan......") },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            // Menggunakan LightSteelBlue untuk background TextField
                            focusedContainerColor = LightSteelBlue,
                            unfocusedContainerColor = LightSteelBlue,
                            disabledContainerColor = LightSteelBlue,
                            errorContainerColor = LightSteelBlue,
                            // Atur warna teks placeholder agar terlihat di atas LightSteelBlue
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            errorPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            // Atur warna teks input
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            errorTextColor = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        }
    }
}