package com.aknaf.utbk_snbt.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class SubjectSelectionScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val subjects = listOf(
            "Penalaran Umum" to "PU",
            "Penget. Kuantitatif" to "PK",
            "Penalaran Matematika" to "PM",
            "Penget. & Pemahaman Umum" to "PPU",
            "KMBM" to "KMBM",
            "Literasi Bhs. Indonesia" to "LBI",
            "Literasi Bhs. Inggris" to "LBE"
        )

        // PAKSA BACKGROUND LAYAR TERANG
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF8F9FA)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Judul Atas
                Text(
                    text = "Simulasi UTBK-SNBT",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF2B2B6E), // Biru Tua
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Pilih sub-tes yang ingin kamu kerjakan sekarang:",
                    color = Color.DarkGray, // 🚀 Teks abu-abu gelap
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subjects) { (name, code) ->
                        // --- [FIX: KARTU PILIHAN MATERI] ---
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navigator.push(DynamicQuizScreen(code)) },
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White // 🚀 PAKSA KARTU PUTIH
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black // 🚀 FIX: PAKSA TULISAN HITAM
                                    )
                                    Text(
                                        text = "Start Questions",
                                        fontSize = 12.sp,
                                        color = Color.Gray // 🚀 Teks keterangan abu-abu
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color(0xFF2B2B6E) // Icon warna biru tua
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}