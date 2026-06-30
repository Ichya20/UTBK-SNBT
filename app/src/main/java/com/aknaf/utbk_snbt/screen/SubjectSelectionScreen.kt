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
import com.aknaf.utbk_snbt.model.ScorePolicy
import com.aknaf.utbk_snbt.model.SubjectCatalog

class SubjectSelectionScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF8F9FA)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Simulasi UTBK-SNBT",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF2B2B6E),
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "Pilih subtes yang ingin kamu kerjakan:",
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEAF4FF)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Sistem penilaian latihan",
                            color = Color(0xFF2B2B6E),
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Setiap jawaban benar bernilai " +
                                "${ScorePolicy.POINTS_PER_CORRECT} poin. " +
                                "Hasil semua subtes terbaru akan dijumlahkan " +
                                "di halaman Riwayat & Progress.",
                            color = Color(0xFF333333),
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Status yang ditampilkan adalah target latihan, " +
                                "bukan prediksi resmi kelulusan UTBK-SNBT.",
                            color = Color(0xFF666666),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = SubjectCatalog.subjects,
                        key = { it.code }
                    ) { subject ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navigator.push(
                                        DynamicQuizScreen(subject.code)
                                    )
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                                verticalAlignment =
                                    Alignment.CenterVertically,
                                horizontalArrangement =
                                    Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = subject.name,
                                        style = MaterialTheme
                                            .typography
                                            .bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )

                                    Text(
                                        text = "Mulai latihan • ${subject.code}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }

                                Icon(
                                    imageVector =
                                        Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color(0xFF2B2B6E)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
