package com.aknaf.utbk_snbt

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class Screen1 : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val context = androidx.compose.ui.platform.LocalContext.current

        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Background Image (Tetap statis di belakang)
            Image(
                painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.colorMatrix(colorMatrix = ColorMatrix().apply { Color.Black.alpha })
            )

            // 2. Semua Konten Masuk LazyColumn Biar Bisa Di-scroll Bebas
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 30.dp,
                    bottom = 40.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- BAGIAN HEADER (Profil & Teks) ---
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable._bf5ac830_dca5_491e_8fd1_2777f92e54bb_removebg_preview),
                            contentDescription = null,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "UTBK-SNBT",
                            color = Color.Black,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Today is a good day to learn something new",
                            color = Color.Black,
                            fontStyle = FontStyle.Normal,
                            fontSize = 15.sp,
                        )
                    }
                }

                // --- BAGIAN BANNER ---
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
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
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

                // --- BAGIAN GRID MENU ---
                // Baris 1: Materi & Simulasi
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        MenuBox(
                            title = "Materi",
                            resId = R.drawable.book_pile_clipart_transparent_png_hd__pile_of_books_3d_icon_education_and_student_concept__education__b_removebg_preview,
                            modifier = Modifier.weight(1f)
                        ) { navigator?.push(ScreenMateri()) }

                        MenuBox(
                            title = "Simulasi",
                            resId = R.drawable.buku_catatan_dan_ikon_pensil_3d__catatan__notes__memeriksa_png_dan_vektor_dengan_background_transparan__removebg_preview__1_,
                            modifier = Modifier.weight(1f)
                        ) { navigator?.push(LatihanSoal()) }
                    }
                }

                // Baris 2: Tips & Jadwal
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

                // Baris 3: Motivasi & Tentang
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

                // --- BAGIAN TOMBOL AI & LOGOUT ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Tombol Tanya AI (Lebar 60%)
                        androidx.compose.material3.Button(
                            onClick = {
                                val intent = android.content.Intent(context, ChatActivity::class.java)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(0.6f)
                                .height(55.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(android.graphics.Color.parseColor("#57BF4B"))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "💬 Tanya AI",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        // Tombol Log Out (Lebar 40%)
                        androidx.compose.material3.Button(
                            onClick = {
                                com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                                val intent = android.content.Intent(context, ActivityLogin::class.java)
                                intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .weight(0.4f)
                                .height(55.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(android.graphics.Color.parseColor("#FF4C4C"))
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Log Out",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Fungsi pembantu biar kodingan kotak menu nggak diulang-ulang panjang
    @Composable
    fun MenuBox(title: String, resId: Int, modifier: Modifier, onClick: () -> Unit) {
        Box(
            modifier = modifier
                .height(140.dp) // Dibuat sedikit lebih proporsional
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Green)
                .clickable { onClick() }
        ) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)), // Menggunakan alpha yang sama dengan kodingan awal
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                            startY = 0f, endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            Text(
                text = title,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}