package com.aknaf.utbk_snbt

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.aknaf.utbk_snbt.screen.ScoreHistoryScreen
import com.aknaf.utbk_snbt.screen.SubjectSelectionScreen
import com.aknaf.utbk_snbt.viewmodel.MotivationViewModel

class Screen1 : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val context = androidx.compose.ui.platform.LocalContext.current

        val motivationVm: MotivationViewModel = viewModel()

        // 🚀 REVISI POIN 4: Jalankan Fungsi Cek Update Otomatis saat aplikasi dibuka
        CheckForUpdates(context = context)

        // 🚀 TRIGGER: Setiap kali layar Home muncul (Unit), paksa ViewModel nyari quote baru
        LaunchedEffect(Unit) {
            motivationVm.fetchRandomQuote()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Background Image (Sama kayak lama)
            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.colorMatrix(colorMatrix = ColorMatrix().apply { Color.Black.alpha })
            )

            // 2. Semua Konten Masuk LazyColumn
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp, end = 20.dp, top = 30.dp, bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- 🚀 BAGIAN HEADER RUMBAK TOTAL (UI BAGUS & ANIMASI) ---
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable._bf5ac830_dca5_491e_8fd1_2777f92e54bb_removebg_preview),
                                contentDescription = null,
                                modifier = Modifier.size(70.dp).clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Hello, Future Scholar! 🎓",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Ready to build your future?",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 🚀 UI BARU: Card Motivasi Premium
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White // Background Card Putih Bersih
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            shape = RoundedCornerShape(24.dp) // Sudut super tumpul, modern
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    // Gradasi tipis di pojok card
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF57BF4B).copy(alpha = 0.05f), Color.Transparent),
                                        )
                                    )
                            ) {
                                // Ikon Quotes (Wajib ada icon quotes di drawable, kalau ngga ada, hapus Row ini)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.baseline_format_quote_24), // 👈 PASTIIN ICON INI ADA DI DRAWABLE
                                        contentDescription = null,
                                        tint = Color(0xFF57BF4B),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Motivation of the Day",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF57BF4B),
                                        fontSize = 14.sp,
                                        letterSpacing = 1.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // 🚀 ANIMASI: Efek Crossfade (Memudar Halus) pas teks berubah
                                Crossfade(
                                    targetState = motivationVm.currentQuote.value,
                                    animationSpec = tween(durationMillis = 800) // Durasi animasi 0.8 detik
                                ) { quoteText ->
                                    Text(
                                        text = quoteText,
                                        color = Color.Black,
                                        lineHeight = 26.sp,
                                        fontStyle = FontStyle.Normal,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }

                // --- BAGIAN BANNER (Sama kayak lama) ---
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(color = Color(android.graphics.Color.parseColor("#57BF4B"))),
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.autodidacts_removebg_preview),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp).clip(CircleShape)
                            )
                            Text(
                                text = "Bimbingan Belajar UTBK",
                                modifier = Modifier.padding(start = 12.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            )
                        }
                    }
                }

                // --- BAGIAN GRID MENU (Sama kayak lama) ---
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MenuBox(
                            title = "Materi",
                            resId = R.drawable.book_pile_clipart_transparent_png_hd__pile_of_books_3d_icon_education_and_student_concept__education__b_removebg_preview,
                            modifier = Modifier.weight(1f)
                        ) { navigator?.push(ScreenMateri()) }

                        MenuBox(
                            title = "Simulasi (New)",
                            resId = R.drawable.buku_catatan_dan_ikon_pensil_3d__catatan__notes__memeriksa_png_dan_vektor_dengan_background_transparan__removebg_preview__1_,
                            modifier = Modifier.weight(1f)
                        ) { navigator?.push(SubjectSelectionScreen()) }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MenuBox(
                            title = "Tips",
                            resId = R.drawable.premium_vector___copywriting_writing_icon_creative_writing_and_storytelling_3d_vector_illustration_removebg_preview,
                            modifier = Modifier.weight(1f)
                        ) { navigator?.push(Tips()) }

                        MenuBox(
                            title = "Jadwal",
                            resId = R.drawable.premium_psd___calendar_date_with_alarm_clock_front_view_3d_rendering_illustration_icon_with_transparent_removebg_preview,
                            modifier = Modifier.weight(1f)
                        ) { navigator?.push(Jadwal()) }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MenuBox(
                            title = "Motivasi",
                            resId = R.drawable.premium_vector___vector_cartoon_hand_holds_pencil_isolated_on_white_background__removebg_preview,
                            modifier = Modifier.weight(1f)
                        ) { navigator?.push(Motivasi()) }

                        MenuBox(
                            title = "Tentang",
                            resId = R.drawable.help__3d_icon_download_in_png__obj_or_blend_format_removebg_preview,
                            modifier = Modifier.weight(1f)
                        ) { navigator?.push(Tentang()) }
                    }
                }

                // --- BAGIAN RIWAYAT (Sama kayak lama) ---
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clickable { navigator?.push(ScoreHistoryScreen()) },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2B2B6E)),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "📊 Riwayat & Progress", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Cek perkembangan nilai simulasimu", color = Color(0xFFD1E3FF), fontSize = 12.sp)
                            }
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                // --- BAGIAN TOMBOL AI & LOGOUT (Sama kayak lama) ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                val intent = android.content.Intent(context, ChatActivity::class.java)
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(0.6f).height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(android.graphics.Color.parseColor("#57BF4B"))),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("💬 Tanya AI", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        Button(
                            onClick = {
                                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                                val intent = android.content.Intent(context, ActivityLogin::class.java)
                                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(0.4f).height(55.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(android.graphics.Color.parseColor("#FF4C4C"))),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Log Out", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }

                // --- 🚀 REVISI POIN 2: TAG TEKS VERSI ALPHA/BETA DI PALING BAWAH ---
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VERSION 1.0.0 - BETA",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFD32F2F), // Badge Merah gelap
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun MenuBox(title: String, resId: Int, modifier: Modifier, onClick: () -> Unit) {
        Box(
            modifier = modifier.height(140.dp).clip(RoundedCornerShape(20.dp)).background(Color.Green).clickable { onClick() }
        ) {
            Image(
                painter = painterResource(id = resId), contentDescription = null,
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier.fillMaxSize().background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                        startY = 0f, endY = Float.POSITIVE_INFINITY
                    )
                )
            )
            Text(title, modifier = Modifier.align(Alignment.BottomCenter).padding(10.dp), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }

    // --- 🚀 REVISI POIN 4: FUNGSI CEK UPDATE OTOMATIS VIA FIRESTORE ---
    @Composable
    fun CheckForUpdates(context: android.content.Context) {
        val currentVersionCode = 1 // Sesuai dengan versionCode di build.gradle.kts kamu saat ini
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        androidx.compose.runtime.LaunchedEffect(Unit) {
            db.collection("app_version").document("version_info")
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val latestVersionCode = document.getLong("versionCode")?.toInt() ?: 1
                        val downloadUrl = document.getString("downloadUrl") ?: ""

                        if (latestVersionCode > currentVersionCode && downloadUrl.isNotEmpty()) {
                            val builder = android.app.AlertDialog.Builder(context)
                            builder.setTitle("Update Tersedia!")
                            builder.setMessage("Versi aplikasi terbaru sudah rilis. Yuk update biar fiturnya makin lengkap tanpa colok USB!")
                            builder.setPositiveButton("Update Sekarang") { _, _ ->
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(downloadUrl))
                                context.startActivity(intent)
                            }
                            builder.setCancelable(false)
                            builder.show()
                        }
                    }
                }
        }
    }
}